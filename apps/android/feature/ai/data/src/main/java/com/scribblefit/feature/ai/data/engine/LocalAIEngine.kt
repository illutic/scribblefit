package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.google.mlkit.genai.common.FeatureStatus
import com.google.mlkit.genai.prompt.GenerativeModel
import com.google.mlkit.genai.prompt.TextPart
import com.google.mlkit.genai.prompt.generateContentRequest
import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
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
) : LLMEngine, AnalysisEngine {

    private val logger = LoggerFactory.getLogger(javaClass)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> = runCatching {
        val content = callLocalAI(systemPrompt, rawText)
        try {
            val parsedWorkoutDto = json.decodeFromString<ParsedWorkoutDto>(content)
            parsedWorkoutDto.toDomain()
        } catch (e: Exception) {
            throw AIParsingException(
                rawText = rawText,
                error = "Hallucination: ${e.message}",
                cause = e
            )
        }
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> = runCatching {
        val content = callLocalAI(AnalysisPrompts.getSuggestionPrompt(context), "Generate suggestion.")
        json.decodeFromString<SuggestionDto>(content).toDomain()
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> = runCatching {
        val content = callLocalAI(AnalysisPrompts.getSummaryPrompt(period.name, workoutData), "Generate summary.")
        json.decodeFromString<SummaryDto>(content).toDomain(period)
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> = runCatching {
        val content = callLocalAI(AnalysisPrompts.getExerciseInsightPrompt(exerciseName, historyData), "Analyze $exerciseName.")
        json.decodeFromString<ExerciseInsightDto>(content).toDomain(exerciseName)
    }

    private suspend fun callLocalAI(prompt: String, userMessage: String): String {
        val status = generativeModel.checkStatus()
        if (status != FeatureStatus.AVAILABLE) {
            throw Exception("Local AI Engine is not available (Status: $status).")
        }

        val fullPrompt = "$prompt\n\nUser Message:\n$userMessage"
        val request = generateContentRequest(TextPart(fullPrompt)) { }
        
        val response = generativeModel.generateContent(request)
        val content = response.candidates.firstOrNull()?.text 
            ?: throw Exception("Empty response from Local Gemini Nano")
        
        return content.trim()
            .removePrefix("```json")
            .removeSuffix("```")
            .trim()
    }
    
    suspend fun isAvailable(): Boolean = runCatching {
        generativeModel.checkStatus() == FeatureStatus.AVAILABLE
    }.getOrDefault(false)
}
