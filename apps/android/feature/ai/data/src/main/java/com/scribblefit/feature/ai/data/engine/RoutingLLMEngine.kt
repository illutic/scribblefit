package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.ExercisePerformanceInsight
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult

internal class RoutingLLMEngine(
    private val geminiEngine: LLMEngine,
    private val localEngine: LLMEngine,
    private val configRepository: ConfigRepository
) : LLMEngine {

    private suspend fun getActiveEngine(): LLMEngine {
        val config = configRepository.config.value
        return when (config.preferredLlmProvider) {
            LLMProvider.GEMINI -> geminiEngine
            LLMProvider.LOCAL -> {
                if (localEngine.isSupported()) {
                    localEngine
                } else {
                    geminiEngine
                }
            }
        }
    }

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> {
        return getActiveEngine().parseWorkout(rawText)
    }

    override suspend fun generateInsightsSummary(exercises: List<Exercise>): Result<List<AIInsight>> {
        return getActiveEngine().generateInsightsSummary(exercises)
    }

    override suspend fun generateExerciseInsight(history: String): Result<ExercisePerformanceInsight> {
        return getActiveEngine().generateExerciseInsight(history)
    }

    override suspend fun isSupported(): Boolean = true
}
