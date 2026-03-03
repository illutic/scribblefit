package com.scribblefit.core.ai.security

interface SecureKeyStorage {
    suspend fun saveApiKey(key: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}
