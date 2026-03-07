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
        return getActiveEngine().parseWorkout(rawText)
    }

    override suspend fun generateSuggestion(context: String): Result<AnalysisSuggestion> {
        return getActiveAnalysisEngine().generateSuggestion(context)
    }

    override suspend fun generateSummary(period: SummaryPeriod, workoutData: String): Result<AnalysisSummary> {
        return getActiveAnalysisEngine().generateSummary(period, workoutData)
    }

    override suspend fun generateExerciseInsight(exerciseName: String, historyData: String): Result<ExerciseInsight> {
        return getActiveAnalysisEngine().generateExerciseInsight(exerciseName, historyData)
    }

    private suspend fun getActiveEngine(): LLMEngine {
        val config = configRepository.getConfig().first()
        val provider = config?.preferredLlmProvider ?: LLMProvider.PROXY
        
        return when (provider) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.GEMINI -> geminiAIEngine
            LLMProvider.LOCAL -> localAIEngine
            LLMProvider.PROXY -> proxyEngine
        }
    }

    private suspend fun getActiveAnalysisEngine(): AnalysisEngine {
        val engine = getActiveEngine()
        return if (engine is AnalysisEngine) engine else {
            // Fallback or throw if selected engine doesn't support analysis
            throw IllegalStateException("Selected engine ${engine.javaClass.simpleName} does not support analysis")
        }
    }
}
