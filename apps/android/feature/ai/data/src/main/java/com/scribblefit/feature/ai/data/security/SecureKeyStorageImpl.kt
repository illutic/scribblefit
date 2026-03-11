package com.scribblefit.feature.ai.data.security

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.core.content.edit
import com.scribblefit.feature.ai.domain.security.SecureKeyStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFS_FILE = "scribblefit_secure_prefs"
private const val KEY_API_KEY = "api_key"
private const val KEYSTORE_ALIAS = "scribblefit_aes_key"
private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
private const val GCM_TAG_LENGTH = 128
private const val KEY_SIZE = 256

@Singleton
internal class SecureKeyStorageImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : SecureKeyStorage {
    private val prefs by lazy {
        context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE)
    }

    override fun saveApiKey(key: String): Result<Unit> = runCatching {
        prefs.edit { putString(KEY_API_KEY, encrypt(key)) }
    }

    override fun getApiKey(): String? =
        prefs.getString(KEY_API_KEY, null)?.let { decrypt(it) }

    override fun clearApiKey(): Result<Unit> = runCatching {
        prefs.edit { remove(KEY_API_KEY) }
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
        val iv = cipher.iv
        val ciphertext = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val ivEncoded = Base64.encodeToString(iv, Base64.NO_WRAP)
        val dataEncoded = Base64.encodeToString(ciphertext, Base64.NO_WRAP)
        return "$ivEncoded:$dataEncoded"
    }

    private fun decrypt(encoded: String): String {
        val parts = encoded.split(":")
        val iv = Base64.decode(parts[0], Base64.NO_WRAP)
        val ciphertext = Base64.decode(parts[1], Base64.NO_WRAP)
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        cipher.init(
            Cipher.DECRYPT_MODE,
            getOrCreateSecretKey(),
            GCMParameterSpec(GCM_TAG_LENGTH, iv)
        )
        return cipher.doFinal(ciphertext).toString(Charsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).also { it.load(null) }
        keyStore.getKey(KEYSTORE_ALIAS, null)?.let { return it as SecretKey }
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    KEYSTORE_ALIAS,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(KEY_SIZE)
                    .build()
            )
        }.generateKey()
    }
}
