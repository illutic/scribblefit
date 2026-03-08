package com.scribblefit.feature.ai.data.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_FILE = "scribblefit_secure_prefs"
private const val KEY_API_KEY = "api_key"

@Singleton
class SecureKeyStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecureKeyStorage {

    private val prefs by lazy {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            PREFS_FILE,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override suspend fun saveApiKey(key: String): Result<Unit> = runCatching {
        prefs.edit().putString(KEY_API_KEY, key).apply()
    }

    override suspend fun getApiKey(): String? = prefs.getString(KEY_API_KEY, null)

    override suspend fun clearApiKey(): Result<Unit> = runCatching {
        prefs.edit().remove(KEY_API_KEY).apply()
    }
}
