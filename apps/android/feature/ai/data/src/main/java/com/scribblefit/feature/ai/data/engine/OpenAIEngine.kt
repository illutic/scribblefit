package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.feature.ai.data.entity.WorkoutDto
import com.scribblefit.feature.ai.data.entity.toDomain
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import com.scribblefit.feature.ai.domain.ParsingStatus
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val OPENAI_BASE_URL = "https://api.openai.com/v1"

internal class OpenAIEngine(
    private val httpClient: HttpClient,
    private val secureKeyStorage: SecureKeyStorage,
    private val json: Json,
    private val configRepository: ConfigRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : LLMEngine {
    private val config get() = configRepository.config.value
    private val apiKey get() = secureKeyStorage.getApiKey() ?: error("No API key provided")

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> = runCatching {
        val startMs = System.currentTimeMillis()
        val model = config.preferredModel ?: error("No Model selected")
        val responseText = callOpenAI(apiKey, model, "${config.parsePrompt}\n\nInput: $rawText")
        val workout = json.decodeFromString<WorkoutDto>(responseText)
        ParsedWorkoutResult(
            workout = workout.toDomain(),
            rawText = rawText,
            status = ParsingStatus.SUCCESS,
            modelUsed = model,
            processingTimeMs = System.currentTimeMillis() - startMs
        )
    }

    private suspend fun callOpenAI(apiKey: String, model: String, userPrompt: String): String =
        withContext(coroutineDispatcher) {
            @Serializable
            data class Message(val role: String, val content: String)

            @Serializable
            data class ResponseFormat(val type: String)

            @Serializable
            data class ChatRequest(
                val model: String,
                val messages: List<Message>,
                @SerialName("response_format")
                val responseFormat: ResponseFormat
            )

            @Serializable
            data class Choice(val message: Message)

            @Serializable
            data class ChatResponse(val choices: List<Choice>)

            val request = ChatRequest(
                model = model,
                messages = listOf(Message("user", userPrompt)),
                responseFormat = ResponseFormat("json_object")
            )
            val response = httpClient.post("$OPENAI_BASE_URL/chat/completions") {
                header(HttpHeaders.Authorization, "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val chatResponse = response.body<ChatResponse>()
            chatResponse.choices.firstOrNull()?.message?.content ?: ""
        }
}
