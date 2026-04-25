package com.scribblefit.feature.settings.domain

class ClearUserDataUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke() {
        repository.clearAllUserData()
    }
}
