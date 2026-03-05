package com.scribblefit.api.features.parser

import com.scribblefit.api.features.config.ConfigService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

class OpenAiParserService(
    private val configService: ConfigService,
    private val apiKey: String
) : AiParserService {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun parseWorkout(rawText: String, prompt: String?): ParsedWorkoutDto {
        val systemPrompt = prompt ?: configService.getPromptConfig().promptText
        
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
            logger.error("OpenAI API error: ${response.status} - $errorBody")
            error("Failed to call OpenAI API: ${response.status}")
        }

        val openAiResponse = response.body<OpenAIResponse>()
        val content = openAiResponse.choices.firstOrNull()?.message?.content
            ?: error("Empty response from OpenAI")

        return try {
            json.decodeFromString<ParsedWorkoutDto>(content)
        } catch (e: Exception) {
            logger.error("Failed to parse OpenAI JSON response: $content", e)
            error("Invalid JSON structure from AI")
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
