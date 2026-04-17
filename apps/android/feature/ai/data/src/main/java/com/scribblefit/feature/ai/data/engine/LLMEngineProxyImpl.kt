package com.scribblefit.feature.ai.data.engine

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.ai.domain.LocalLLMEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LLMEngineProxyImpl(
    private val geminiEngine: LLMEngine,
    private val localEngine: LLMEngine,
    configRepository: ConfigRepository
) : LLMEngineProxy {

    override val underlyingEngine: Flow<LLMEngine> = configRepository.config.map { config ->
        when (config.preferredLlmProvider) {
            LLMProvider.GEMINI -> geminiEngine
            LLMProvider.LOCAL -> {
                if (localEngine is LocalLLMEngine && localEngine.isSupported()) {
                    localEngine
                } else {
                    // Fallback to Gemini if local is requested but not supported/downloaded
                    geminiEngine
                }
            }
            else -> geminiEngine
        }
    }
}
