package com.scribblefit.feature.profile.domain.repository

import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.profile.domain.model.AppSettings
import com.scribblefit.feature.profile.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserStats(): Flow<UserStats>
}

interface SettingsRepository {
    fun getSettings(): Flow<AppSettings>
    suspend fun updateSettings(settings: AppSettings)
    suspend fun clearAllData()
}

interface ModelRepository {
    suspend fun fetchModels(provider: LLMProvider, apiKey: String): List<String>
}
