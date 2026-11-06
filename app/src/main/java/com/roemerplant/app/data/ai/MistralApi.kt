package com.roemerplant.app.data.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Double? = null,
    val response_format: ResponseFormat? = null
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: List<ContentPart>
)

@Serializable
data class ContentPart(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
) {
    companion object {
        fun text(value: String) = ContentPart(type = "text", text = value)
        fun image(base64DataUrl: String) = ContentPart(type = "image_url", image_url = ImageUrl(base64DataUrl))
    }
}

@Serializable
data class ImageUrl(val url: String)

@Serializable
data class ResponseFormat(
    val type: String,
    val json_schema: JsonSchemaSpec? = null
)

@Serializable
data class JsonSchemaSpec(
    val name: String,
    val schema: JsonElement
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<Choice> = emptyList(),
    val model: String? = null
)

@Serializable
data class Choice(
    val index: Int = 0,
    val message: AssistantMessage? = null
)

@Serializable
data class AssistantMessage(
    val role: String? = null,
    val content: String? = null
)

@Serializable
data class MistralError(
    val message: String? = null,
    val code: Int? = null
)

@Serializable
data class MistralErrorBody(
    val error: MistralError? = null
)

@Serializable
data class ModelsResponse(
    val data: List<ModelInfo> = emptyList()
)

@Serializable
data class ModelInfo(
    val id: String
)

interface MistralApi {
    @POST("chat/completions")
    suspend fun chatCompletions(@Body request: ChatCompletionRequest): ChatCompletionResponse

    @GET("models")
    suspend fun models(): ModelsResponse
}
