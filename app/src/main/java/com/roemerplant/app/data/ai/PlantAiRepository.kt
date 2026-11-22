package com.roemerplant.app.data.ai

import com.roemerplant.app.data.model.Plant
import com.roemerplant.app.data.settings.SettingsRepository
import com.roemerplant.app.util.PlantIntelligence
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlantAiRepository @Inject constructor(
    private val settings: SettingsRepository
) {
    private val json = Json { ignoreUnknownKeys = true; coerceInputValues = true }

    private val api: MistralApi = run {
        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val key = runCatching {
                    kotlinx.coroutines.runBlocking { settings.mistralApiKey.first() }
                }.getOrNull().orEmpty()
                val request = chain.request().newBuilder()
                    .header("Authorization", "Bearer $key")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()
        Retrofit.Builder()
            .baseUrl("https://api.mistral.ai/v1/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MistralApi::class.java)
    }

    suspend fun loadModels(): Result<List<String>> = runCatching {
        api.models().data.map { it.id }.sorted()
    }

    suspend fun analyzePlant(base64Jpeg: String, userHint: String = ""): Result<PlantAnalysis> =
        runCatching {
            val season = PlantIntelligence.currentSeason().label
            val prompt = buildString {
                append(
                    "Du bist ein erfahrener Botaniker und Pflanzenexperte. Analysiere die Pflanze auf dem Foto. " +
                        "Aktuelle Jahreszeit: $season. " +
                        "Bestimme die Pflanzenart, erstelle eine kurze Beschreibung, " +
                        "vollständige Pflegeanweisungen und eine ehrliche Gesundheitsanalyse. " +
                        "Beantworte alles auf Deutsch. Wenn du die Pflanze nicht sicher bestimmen kannst, " +
                        "schreibe es ehrlich und gib die wahrscheinlichsten Möglichkeiten an."
                )
                if (userHint.isNotBlank()) {
                    append(" Zusatzinfo vom Nutzer: $userHint")
                }
            }
            val request = ChatCompletionRequest(
                model = settings.mistralModel.first(),
                temperature = 0.2,
                messages = listOf(
                    ChatMessage(
                        role = "user",
                        content = listOf(
                            ContentPart.text(prompt),
                            ContentPart.image("data:image/jpeg;base64,$base64Jpeg")
                        )
                    )
                ),
                response_format = ResponseFormat(
                    type = "json_schema",
                    json_schema = JsonSchemaSpec(
                        name = "plant_analysis",
                        schema = json.parseToJsonElement(ANALYSIS_SCHEMA)
                    )
                )
            )
            val response = api.chatCompletions(request)
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Leere Antwort von Mistral")
            json.decodeFromString<PlantAnalysis>(content)
        }.recoverMistralErrors()

    suspend fun generateCareProfileByName(plantNameOrSpecies: String): Result<PlantAnalysis> =
        runCatching {
            val season = PlantIntelligence.currentSeason().label
            val prompt = "Du bist ein erfahrener Botaniker. Erstelle einen vollständigen, praxisnahen " +
                "Pflege-Steckbrief für die Pflanze „$plantNameOrSpecies“ (aktuelle Jahreszeit: $season). " +
                "Ergänze den botanischen Artnamen, den deutschen Namen, die Pflanzenfamilie, eine kurze " +
                "Beschreibung und konkrete Pflegehinweise (Licht, Gießen, Luftfeuchtigkeit, Temperatur, " +
                "Düngen, Substrat) auf Deutsch. Da kein Foto vorliegt, setze health.status auf „unbekannt“ " +
                "und gib 2–3 allgemeine Vorsorge-Tipps unter health.instructions."
            val request = ChatCompletionRequest(
                model = settings.mistralModel.first(),
                temperature = 0.2,
                messages = listOf(
                    ChatMessage(
                        role = "user",
                        content = listOf(ContentPart.text(prompt))
                    )
                ),
                response_format = ResponseFormat(
                    type = "json_schema",
                    json_schema = JsonSchemaSpec(
                        name = "plant_analysis",
                        schema = json.parseToJsonElement(ANALYSIS_SCHEMA)
                    )
                )
            )
            val response = api.chatCompletions(request)
            val content = response.choices.firstOrNull()?.message?.content
                ?: throw IllegalStateException("Leere Antwort von Mistral")
            json.decodeFromString<PlantAnalysis>(content)
        }.recoverMistralErrors()

    suspend fun askPlantAdvisor(plant: Plant, question: String): Result<String> =
        runCatching {
            val season = PlantIntelligence.currentSeason().label
            val contextPrompt = buildString {
                appendLine("Du bist ein freundlicher, präziser Botaniker in der Android-App RömerPlant.")
                appendLine("Beantworte die Frage des Nutzers kurz, konkret und praxisnah auf Deutsch (max. 5 Sätze oder kurze Stichpunkte).")
                appendLine("Aktuelle Jahreszeit: $season")
                appendLine("Pflanzen-Kontext:")
                appendLine("- Name: ${plant.name.ifBlank { "Unbekannt" }}")
                if (plant.species.isNotBlank()) appendLine("- Art: ${plant.species}")
                if (plant.family.isNotBlank()) appendLine("- Familie: ${plant.family}")
                if (plant.careLight.isNotBlank()) appendLine("- Licht: ${plant.careLight}")
                if (plant.careWater.isNotBlank()) appendLine("- Gießen: ${plant.careWater}")
                if (plant.healthStatus.isNotBlank()) appendLine("- Gesundheitsstatus: ${plant.healthStatus}")
                if (plant.healthAnalysis.isNotBlank()) appendLine("- Letzte Analyse: ${plant.healthAnalysis}")
                appendLine()
                append("Frage: $question")
            }
            val request = ChatCompletionRequest(
                model = settings.mistralModel.first(),
                temperature = 0.4,
                messages = listOf(
                    ChatMessage(
                        role = "user",
                        content = listOf(ContentPart.text(contextPrompt))
                    )
                )
            )
            val response = api.chatCompletions(request)
            response.choices.firstOrNull()?.message?.content?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Leere Antwort von Mistral")
        }.recoverMistralErrors()

    private fun <T> Result<T>.recoverMistralErrors(): Result<T> = recoverCatching { error ->
        if (error is HttpException) {
            val body = error.response()?.errorBody()?.string().orEmpty()
            when (error.code()) {
                401 -> throw IllegalStateException(
                    "Ungültiger Mistral-API-Key (401). Bitte den Key im Profil prüfen."
                )
                404 -> throw IllegalStateException(
                    "Modell nicht gefunden (404). Bitte im Profil ein aktuelles KI-Modell wählen."
                )
                429 -> throw IllegalStateException(
                    "Anfragelimit bei Mistral erreicht (429). Bitte kurz warten und erneut versuchen."
                )
                else -> throw IllegalStateException("Mistral-Fehler ${error.code()}: $body")
            }
        }
        throw error
    }

    companion object {
        private const val ANALYSIS_SCHEMA = """{
            "type": "object",
            "properties": {
                "species": {"type": "string"},
                "commonName": {"type": "string"},
                "family": {"type": "string"},
                "description": {"type": "string"},
                "care": {
                    "type": "object",
                    "properties": {
                        "light": {"type": "string"},
                        "water": {"type": "string"},
                        "humidity": {"type": "string"},
                        "temperature": {"type": "string"},
                        "fertilizer": {"type": "string"},
                        "soil": {"type": "string"}
                    },
                    "required": ["light", "water", "humidity", "temperature", "fertilizer", "soil"],
                    "additionalProperties": false
                },
                "health": {
                    "type": "object",
                    "properties": {
                        "status": {
                            "type": "string",
                            "enum": ["gesund", "leicht gestresst", "krank", "Schädlingsbefall", "unbekannt"]
                        },
                        "analysis": {"type": "string"},
                        "instructions": {
                            "type": "array",
                            "items": {"type": "string"}
                        }
                    },
                    "required": ["status", "analysis", "instructions"],
                    "additionalProperties": false
                }
            },
            "required": ["species", "commonName", "family", "description", "care", "health"],
            "additionalProperties": false
        }"""
    }
}
