package com.scribblefit.feature.settings.domain

import com.scribblefit.feature.ai.domain.LLMEngine

class CheckLocalSupportUseCase(
    private val localEngine: LLMEngine
) {
    suspend operator fun invoke(): Boolean {
        return localEngine.isSupported()
    }
}
