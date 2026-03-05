package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.model.ParseRequest
import com.scribblefit.core.network.model.ParsedWorkoutDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class OpenAIEngine(
    private val client: HttpClient,
    private val apiKey: String,
    private val systemPrompt: String,
    private val json: Json
) : LLMEngine {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val response = client.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer $apiKey")
            contentType(ContentType.Application.Json)
            setBody(
                OpenAIRequest(
                    model = "gpt-4o-mini",
                    messages = listOf(
                        OpenAIMessage(role = "system", content = systemPrompt),
                        OpenAIMessage(role = "user", content = rawText)
                    ),
                    responseFormat = OpenAIResponseFormat(type = "json_object")
                )
            )
        }

        if (!response.status.isSuccess()) {
            val errorBody = response.bodyAsText()
            throw Exception("OpenAI API error: ${response.status} - $errorBody")
        }

        val openAiResponse = response.body<OpenAIResponse>()
        val content = openAiResponse.choices.firstOrNull()?.message?.content
            ?: throw Exception("Empty response from OpenAI")

        try {
            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
            parsedWorkoutDto.toDomain()
        } catch (e: Exception) {
            logger.error("Hallucination detected: $content", e)
            throw AIParsingException(rawText = rawText, error = "Hallucination: ${e.message}", cause = e)
        }
    }
}

@Serializable
private data class OpenAIRequest(
    val model: String,
    val messages: List<OpenAIMessage>,
    @SerialName("response_format") val responseFormat: OpenAIResponseFormat
)

@Serializable
private data class OpenAIMessage(
    val role: String,
    val content: String
)

@Serializable
private data class OpenAIResponseFormat(
    val type: String
)

@Serializable
private data class OpenAIResponse(
    val choices: List<OpenAIChoice>
)

@Serializable
private data class OpenAIChoice(
    val message: OpenAIMessage
)
