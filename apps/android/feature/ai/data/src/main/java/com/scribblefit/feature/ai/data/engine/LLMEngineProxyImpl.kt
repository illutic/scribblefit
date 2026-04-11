package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LLMEngineProxyImpl(
    private val geminiEngine: LLMEngine,
    private val localEngine: LLMEngine,
    configRepository: ConfigRepository
) : LLMEngineProxy {

    override val underlyingEngine: Flow<LLMEngine> = configRepository.config.map {
        when (it.preferredLlmProvider) {
            LLMProvider.GEMINI -> geminiEngine
            else -> localEngine
        }
    }
}
