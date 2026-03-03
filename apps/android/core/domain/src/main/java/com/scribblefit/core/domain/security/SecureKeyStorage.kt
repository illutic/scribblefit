package com.scribblefit.core.domain.security

interface SecureKeyStorage {
    suspend fun saveApiKey(key: String)
    suspend fun getApiKey(): String?
    suspend fun clearApiKey()
}
