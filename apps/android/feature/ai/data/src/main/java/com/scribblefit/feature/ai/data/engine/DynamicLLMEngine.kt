package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.feature.ai.domain.engine.*
import com.scribblefit.feature.ai.domain.model.*
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Named

class DynamicLLMEngine @Inject constructor(
    @param:Named("openai") private val openAIEngine: LLMEngine,
    @param:Named("gemini") private val geminiAIEngine: LLMEngine,
    @param:Named("proxy") private val proxyEngine: LLMEngine,
    private val localAIEngine: LocalAIEngine,
    private val systemConfigDao: SystemConfigDao
) : LLMEngine, AnalysisEngine {

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkout> {
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
        val config = systemConfigDao.getConfig().firstOrNull()
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
