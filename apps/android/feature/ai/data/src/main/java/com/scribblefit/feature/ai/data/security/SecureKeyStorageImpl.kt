package com.scribblefit.feature.ai.data.security

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureKeyStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SecureKeyStorage {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "scribblefit_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveApiKey(key: String) {
        sharedPreferences.edit { putString(KEY_API_KEY, key) }
    }

    override suspend fun getApiKey(): String? {
        return sharedPreferences.getString(KEY_API_KEY, null)
    }

    override suspend fun clearApiKey() {
        sharedPreferences.edit { remove(KEY_API_KEY) }
    }

    override suspend fun saveAuthToken(token: String) {
        sharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    override suspend fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    override suspend fun clearAuthToken() {
        sharedPreferences.edit { remove(KEY_AUTH_TOKEN) }
    }

    companion object {
        private const val KEY_API_KEY = "ai_api_key"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
}
