package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import javax.inject.Inject

/**
 * Local AI Engine for Android.
 * Leverages on-device Gemini Nano via AICore / ML Kit GenAI Prompt API.
 */
class LocalAIEngine @Inject constructor(
    private val generativeModel: GenerativeModel,
    private val json: Json,
    private val systemPrompt: String
) : LLMEngine {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val status = generativeModel.checkStatus()
        if (status != FeatureStatus.AVAILABLE) {
            throw Exception("Local AI Engine is not available (Status: $status).")
        }

        val fullPrompt = "$systemPrompt\n\nInput Workout:\n$rawText"
        val response = generativeModel.generateContent(fullPrompt)
        
        val content = response.candidates.firstOrNull()?.text 
            ?: throw Exception("Empty response from Local Gemini Nano")
        
        val cleanContent = content.trim()
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()

        try {
            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(cleanContent)
            parsedWorkoutDto.toDomain()
        } catch (e: Exception) {
            logger.error("Hallucination detected in Local response: $cleanContent", e)
            throw AIParsingException(rawText = rawText, error = "Hallucination: ${e.message}", cause = e)
        }
    }
    
    suspend fun isAvailable(): Boolean = runCatching {
        generativeModel.checkStatus() == FeatureStatus.AVAILABLE
    }.getOrDefault(false)

    /**
     * Triggers the download of the local Gemini Nano model if it's not already present.
     */
    suspend fun prepareModel(): Result<Unit> = runCatching {
        val status = generativeModel.checkStatus()
        if (status == FeatureStatus.NOT_AVAILABLE) {
            error("Gemini Nano is not supported on this device.")
        }
        
        if (status == FeatureStatus.DOWNLOADABLE) {
            generativeModel.downloadModel()
        }
    }

    suspend fun getStatus(): FeatureStatus = runCatching {
        generativeModel.checkStatus()
    }.getOrDefault(FeatureStatus.NOT_AVAILABLE)
}
