package com.scribblefit.feature.settings.domain

class TestConnectionUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(apiKey: String): Result<Unit> {
        return repository.testConnection(apiKey)
    }
}
