package com.roemerplant.app.data.ai

import com.roemerplant.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.kotlinxserialization.asConverterFactory
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
                    json_schema = JsonSchemaSpec(name = "plant_analysis", schema = analysisSchema())
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

    private fun analysisSchema() = buildJsonObject {
        put("type", "object")
        put("properties", buildJsonObject {
            put("species", buildJsonObject { put("type", "string") })
            put("commonName", buildJsonObject { put("type", "string") })
            put("description", buildJsonObject { put("type", "string") })
            put("care", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject {
                    put("light", buildJsonObject { put("type", "string") })
                    put("water", buildJsonObject { put("type", "string") })
                    put("humidity", buildJsonObject { put("type", "string") })
                    put("temperature", buildJsonObject { put("type", "string") })
                    put("fertilizer", buildJsonObject { put("type", "string") })
                    put("soil", buildJsonObject { put("type", "string") })
                })
                put("required", buildJsonArray {
                    add("light"); add("water"); add("humidity"); add("temperature"); add("fertilizer"); add("soil")
                })
                put("additionalProperties", false)
            })
            put("health", buildJsonObject {
                put("type", "object")
                put("properties", buildJsonObject {
                    put("status", buildJsonObject {
                        put("type", "string")
                        put("enum", buildJsonArray {
                            add("gesund"); add("leicht gestresst"); add("krank"); add("Schädlingsbefall"); add("unbekannt")
                        })
                    })
                    put("analysis", buildJsonObject { put("type", "string") })
                    put("instructions", buildJsonObject {
                        put("type", "array")
                        put("items", buildJsonObject { put("type", "string") })
                    })
                })
                put("required", buildJsonArray { add("status"); add("analysis"); add("instructions") })
                put("additionalProperties", false)
            })
        })
        put("required", buildJsonArray {
            add("species"); add("commonName"); add("description"); add("care"); add("health")
        })
        put("additionalProperties", false)
    }

    companion object {
        const val MODEL = "pixtral-12b-latest"
    }
}
