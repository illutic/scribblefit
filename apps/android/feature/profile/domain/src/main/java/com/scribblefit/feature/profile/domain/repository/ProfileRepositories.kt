package com.scribblefit.feature.profile.domain.repository

import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.feature.profile.domain.model.UserStats
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserStats(): Flow<UserStats>
}

interface ModelRepository {
    suspend fun fetchModels(provider: LLMProvider, apiKey: String): List<String>
}
