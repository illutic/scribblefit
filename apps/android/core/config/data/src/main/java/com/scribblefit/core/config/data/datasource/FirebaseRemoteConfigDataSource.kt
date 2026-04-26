package com.scribblefit.core.config.data.datasource

import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import com.scribblefit.core.config.domain.RemoteConfig
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory

class FirebaseRemoteConfigDataSource(
    private val firebaseRemoteConfig: FirebaseRemoteConfig
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour
        }
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        firebaseRemoteConfig.setDefaultsAsync(
            mapOf(
                SUGGESTION_PROMPT_KEY to RemoteConfig.SUGGESTION_PROMPT,
                SUMMARY_PROMPT_KEY to RemoteConfig.SUMMARY_PROMPT,
                INSIGHT_PROMPT_KEY to RemoteConfig.INSIGHT_PROMPT,
                PARSE_PROMPT_KEY to RemoteConfig.PARSE_PROMPT
            )
        )
    }

    fun getRemoteConfig(): Flow<RemoteConfig> = callbackFlow {
        val listenerRegistration = firebaseRemoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {
                firebaseRemoteConfig.activate().addOnCompleteListener {
                    trySend(mapToRemoteConfig())
                }
            }

            override fun onError(error: FirebaseRemoteConfigException) {
                logger.error("Error updating remote config: ${error.message}")
            }
        })

        // Initial fetch
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                trySend(mapToRemoteConfig())
            } else {
                logger.warn("Failed to fetch remote config, using defaults/cached")
                trySend(mapToRemoteConfig())
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }.onStart {
        emit(mapToRemoteConfig())
    }

    private fun mapToRemoteConfig(): RemoteConfig {
        return RemoteConfig(
            suggestionPrompt = firebaseRemoteConfig.getString(SUGGESTION_PROMPT_KEY),
            summaryPrompt = firebaseRemoteConfig.getString(SUMMARY_PROMPT_KEY),
            insightPrompt = firebaseRemoteConfig.getString(INSIGHT_PROMPT_KEY),
            parsePrompt = firebaseRemoteConfig.getString(PARSE_PROMPT_KEY)
        )
    }

    companion object {
        private const val SUGGESTION_PROMPT_KEY = "suggestion_prompt"
        private const val SUMMARY_PROMPT_KEY = "summary_prompt"
        private const val INSIGHT_PROMPT_KEY = "insight_prompt"
        private const val PARSE_PROMPT_KEY = "parse_prompt"
    }
}
