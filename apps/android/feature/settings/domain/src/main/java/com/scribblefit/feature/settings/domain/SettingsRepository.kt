package com.scribblefit.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun clearAllUserData()
    suspend fun exportUserData(): Flow<String>
    suspend fun testConnection(apiKey: String): Result<Unit>
}
