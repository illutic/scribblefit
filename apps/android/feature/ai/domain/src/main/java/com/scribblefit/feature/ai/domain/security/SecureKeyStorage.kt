package com.scribblefit.feature.ai.domain.security

interface SecureKeyStorage {
    suspend fun saveApiKey(key: String): Result<Unit>
    suspend fun getApiKey(): String?
    suspend fun clearApiKey(): Result<Unit>
}
