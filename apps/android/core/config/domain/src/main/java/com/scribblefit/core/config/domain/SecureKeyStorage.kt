package com.scribblefit.core.config.domain

interface SecureKeyStorage {
    fun saveApiKey(key: String): Result<Unit>
    fun getApiKey(): String?
    fun clearApiKey(): Result<Unit>
}
