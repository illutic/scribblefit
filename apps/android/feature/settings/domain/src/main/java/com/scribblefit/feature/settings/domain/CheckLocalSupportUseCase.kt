package com.scribblefit.feature.settings.domain

import com.scribblefit.feature.ai.domain.LocalLLMEngine

class CheckLocalSupportUseCase(
    private val localEngine: LocalLLMEngine
) {
    suspend operator fun invoke(): Boolean {
        return localEngine.isSupported()
    }
}
