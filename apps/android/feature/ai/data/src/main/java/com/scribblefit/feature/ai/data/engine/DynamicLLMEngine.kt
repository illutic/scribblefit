package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.engine.AnalysisEngine
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Named

import com.scribblefit.feature.ai.domain.model.*

class DynamicLLMEngine @Inject constructor(
    @param:Named("openai") private val openAIEngine: LLMEngine,
    @param:Named("gemini") private val geminiAIEngine: LLMEngine,
    @param:Named("proxy") private val proxyEngine: LLMEngine,
    private val localAIEngine: LocalAIEngine,
    private val configRepository: ConfigRepository
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): ParsedWorkoutResult {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        
        val engines = getEnginePriorityList(preferredProvider)
        
        var lastResult: ParsedWorkoutResult? = null
        
        for (engine in engines) {
            val result = engine.parseWorkout(rawText)
            if (result.status == ParsingStatus.SUCCESS) {
                return result
            }
            lastResult = result
            // If it's a failure, we continue to the next engine in the priority list
        }
        
        return lastResult ?: ParsedWorkoutResult(
            workout = null,
            rawText = rawText,
            status = ParsingStatus.FAILURE,
            error = "No engines available or all engines failed"
        )
    }

    private fun getEnginePriorityList(preferred: LLMProvider): List<LLMEngine> {
        val baseList = when (preferred) {
            LLMProvider.OPENAI -> listOf(openAIEngine, geminiAIEngine, proxyEngine, localAIEngine)
            LLMProvider.GEMINI -> listOf(geminiAIEngine, openAIEngine, proxyEngine, localAIEngine)
            LLMProvider.PROXY -> listOf(proxyEngine, geminiAIEngine, openAIEngine, localAIEngine)
            LLMProvider.LOCAL -> listOf(localAIEngine, geminiAIEngine, openAIEngine, proxyEngine)
        }
        return baseList
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engines = getAnalysisEnginePriorityList(preferredProvider)
        
        var lastError: Throwable? = null
        for (engine in engines) {
            val result = engine.generateSuggestion(context)
            if (result.isSuccess) return result
            lastError = result.exceptionOrNull()
        }
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engines = getAnalysisEnginePriorityList(preferredProvider)
        
        var lastError: Throwable? = null
        for (engine in engines) {
            val result = engine.generateSummary(period, workoutData)
            if (result.isSuccess) return result
            lastError = result.exceptionOrNull()
        }
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> {
        val config = configRepository.getConfig().first()
        val preferredProvider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        val engines = getAnalysisEnginePriorityList(preferredProvider)
        
        var lastError: Throwable? = null
        for (engine in engines) {
            val result = engine.generateExerciseInsight(exerciseName, historyData)
            if (result.isSuccess) return result
            lastError = result.exceptionOrNull()
        }
        return Result.failure(lastError ?: Exception("All analysis engines failed"))
    }

    private fun getAnalysisEnginePriorityList(preferred: LLMProvider): List<AnalysisEngine> {
        return getEnginePriorityList(preferred).filterIsInstance<AnalysisEngine>()
    }
}
