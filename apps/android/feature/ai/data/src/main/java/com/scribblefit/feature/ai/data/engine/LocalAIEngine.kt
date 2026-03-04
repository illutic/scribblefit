package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.toDomain
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import kotlinx.serialization.json.Json
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

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> {
        return try {
            val status = generativeModel.checkStatus()
            if (status != FeatureStatus.AVAILABLE) {
                return Result.failure(Exception("Local AI Engine is not available (Status: $status)."))
            }

            val fullPrompt = "$systemPrompt\n\nInput Workout:\n$rawText"
            val response = generativeModel.generateContent(fullPrompt)
            
            val content = response.candidates.firstOrNull()?.text 
                ?: throw Exception("Empty response from Local Gemini Nano")
            
            // The model might include markdown code blocks, strip them if present
            val cleanContent = content.trim()
                .removePrefix("```json")
                .removeSuffix("```")
                .trim()

            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(cleanContent)
            Result.success(parsedWorkoutDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun isAvailable(): Boolean {
        return try {
            generativeModel.checkStatus() == FeatureStatus.AVAILABLE
        } catch (e: Exception) {
            false
        }
    }
}
