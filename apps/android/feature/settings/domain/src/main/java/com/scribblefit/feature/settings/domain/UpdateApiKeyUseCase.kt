package com.scribblefit.feature.settings.domain

import com.scribblefit.core.config.domain.SecureKeyStorage

class UpdateApiKeyUseCase(
    private val secureKeyStorage: SecureKeyStorage
) {
    suspend operator fun invoke(apiKey: String): Result<Unit> {
        return secureKeyStorage.saveApiKey(apiKey)
    }
}
