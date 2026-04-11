package com.scribblefit.feature.settings.domain

import com.scribblefit.feature.ai.domain.CloudLLMEngine
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import kotlinx.coroutines.flow.first

class GetAvailableModelsUseCase(
    private val llmEngineProxy: LLMEngineProxy
) {
    suspend operator fun invoke(apiKey: String): Result<List<String>> {
        val llmEngine = llmEngineProxy.underlyingEngine.first() as? CloudLLMEngine
            ?: return Result.failure(IllegalStateException("Current LLM engine does not support fetching available models"))
        return llmEngine.getAvailableModels(apiKey)
    }
}
