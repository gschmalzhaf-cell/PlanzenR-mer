package com.roemerplant.app.data.ai

import com.roemerplant.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
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
            .baseUrl("https://api.mistral.ai/")
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MistralApi::class.java)
    }

    suspend fun analyzePlant(base64Jpeg: String, userHint: String = ""): Result<PlantAnalysis> =
        runCatching {
            val prompt = buildString {
                append(
                    "Du bist ein erfahrener Botaniker und Pflanzenexperte. Analysiere die Pflanze auf dem Foto. " +
                        "Bestimme die Pflanzenart, erstle eine kurze Beschreibung, " +
                        "vollständige Pflegeanweisungen und eine ehrliche Gesundheitsanalyse. " +
                        "Beantworte alles auf Deutsch. Wenn du die Pflanze nicht sicher bestimmen kannst, " +
                        "schreibe es ehrlich und gib die wahrscheinlichsten Möglichkeiten an."
                )
                if (userHint.isNotBlank()) {
                    append(" Zusatzinfo vom Nutzer: $userHint")
                }
            }
            val request = ChatCompletionRequest(
                model = MODEL,
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
        }.recoverCatching { error ->
            if (error is HttpException) {
                val body = error.response()?.errorBody()?.string().orEmpty()
                throw IllegalStateException("Mistral-Fehler ${error.code()}: $body")
            }
            throw error
        }

    companion object {
        const val MODEL = "pixtral-12b-latest"

        private const val ANALYSIS_SCHEMA = """{
            "type": "object",
            "properties": {
                "species": {"type": "string"},
                "commonName": {"type": "string"},
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
            "required": ["species", "commonName", "description", "care", "health"],
            "additionalProperties": false
        }"""
    }
}
