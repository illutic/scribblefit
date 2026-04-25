package com.scribblefit.feature.settings.domain

import kotlinx.coroutines.flow.Flow

class ExportUserDataUseCase(
    private val repository: SettingsRepository
) {
    suspend operator fun invoke(): Flow<String> {
        return repository.exportUserData()
    }
}
