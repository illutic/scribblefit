package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.model.ParsedWorkoutDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Named

class GeminiAIEngine @Inject constructor(
    @Named("base") private val client: HttpClient,
    private val apiKey: String,
    private val systemPrompt: String,
    private val json: Json
) : LLMEngine {

    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:"

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val response = client.post("${baseUrl}generateContent?key=$apiKey") {
            contentType(ContentType.Application.Json)
            setBody(
                GeminiRequest(
                    contents = listOf(
                        GeminiContent(
                            parts = listOf(GeminiPart(text = rawText))
                        )
                    ),
                    systemInstruction = GeminiSystemInstruction(
                        parts = listOf(GeminiPart(text = systemPrompt))
                    ),
                    generationConfig = GeminiGenerationConfig(
                        responseMimeType = "application/json"
                    )
                )
            )
        }.body<GeminiResponse>()

        val content = response.candidates.firstOrNull()?.content?.parts?.firstOrNull()?.text
            ?: throw Exception("Empty response from Gemini")

        val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
        parsedWorkoutDto.toDomain()
    }
}

@Serializable
private data class GeminiRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiSystemInstruction,
    val generationConfig: GeminiGenerationConfig
)

@Serializable
private data class GeminiContent(
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiSystemInstruction(
    val parts: List<GeminiPart>
)

@Serializable
private data class GeminiPart(
    val text: String
)

@Serializable
private data class GeminiGenerationConfig(
    val responseMimeType: String
)

@Serializable
private data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
private data class GeminiCandidate(
    val content: GeminiContent
)
