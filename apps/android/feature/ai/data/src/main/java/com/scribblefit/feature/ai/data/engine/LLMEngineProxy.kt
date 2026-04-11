package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.ai.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class DynamicLLMEngine(
    private val geminiEngine: LLMEngine,
    private val localEngine: LLMEngine,
    configRepository: ConfigRepository,
    coroutineDispatcher: CoroutineDispatcher
) : LLMEngine,
    CoroutineScope by CoroutineScope(coroutineDispatcher + CoroutineName("DynamicLLMEngine")) {
    private val activeEngine = configRepository.config.map {
        when (it.preferredLlmProvider) {
            LLMProvider.GEMINI -> geminiEngine
            else -> localEngine
        }
    }.stateIn(this, SharingStarted.Eagerly, localEngine)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> =
        withContext(coroutineContext) {
            activeEngine.value.parseWorkout(rawText)
        }

    override suspend fun generateInsightsSummary(exercises: List<com.scribblefit.core.model.Exercise>): Result<List<com.scribblefit.core.model.AIInsight>> =
        withContext(coroutineContext) {
            activeEngine.value.generateInsightsSummary(exercises)
        }
}
