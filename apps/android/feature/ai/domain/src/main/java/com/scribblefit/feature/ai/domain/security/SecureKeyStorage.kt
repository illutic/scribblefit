package com.scribblefit.feature.ai.domain.security

interface SecureKeyStorage {
    fun saveApiKey(key: String): Result<Unit>
    fun getApiKey(): String?
    fun clearApiKey(): Result<Unit>
}
