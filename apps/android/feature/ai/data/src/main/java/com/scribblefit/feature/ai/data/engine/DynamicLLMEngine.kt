package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.ParsedWorkoutResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

internal class DynamicLLMEngine(
    private val openAIEngine: LLMEngine,
    private val geminiEngine: LLMEngine,
    private val localEngine: LocalAIEngine,
    configRepository: ConfigRepository,
    coroutineDispatcher: CoroutineDispatcher
) : LLMEngine,
    CoroutineScope by CoroutineScope(coroutineDispatcher + CoroutineName("DynamicLLMEngine")) {
    private val activeEngine = configRepository.config.map {
        when (it.preferredLlmProvider) {
            LLMProvider.OPENAI -> openAIEngine
            LLMProvider.GEMINI -> geminiEngine
            else -> localEngine
        }
    }.stateIn(this, SharingStarted.Eagerly, localEngine)

    override suspend fun parseWorkout(rawText: String): Result<ParsedWorkoutResult> =
        withContext(coroutineContext) {
            activeEngine.value.parseWorkout(rawText)
        }
}
