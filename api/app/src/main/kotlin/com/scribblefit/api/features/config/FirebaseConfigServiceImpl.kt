package com.scribblefit.api.features.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import org.slf4j.LoggerFactory

class FirebaseConfigServiceImpl : ConfigService {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun getPromptConfig(): ConfigResponse {
        return try {
            val template = FirebaseRemoteConfig.getInstance().getTemplate()
            val parameters = template.parameters
            
            val version = parameters["promptVersion"]?.defaultValue?.toString() ?: "unknown"
            val prompt = parameters["promptText"]?.defaultValue?.toString() ?: ""
            
            ConfigResponse(version = version, prompt = prompt)
        } catch (e: Exception) {
            logger.error("Error fetching Firebase Remote Config: ${e.message}")
            // Fallback to a safe minimum or rethrow depending on strategy
            ConfigResponse(version = "error", prompt = "Error loading remote config")
        }
    }

    override fun getMetadata(): MetadataResponse {
        // In a real implementation, we would probably fetch metadata from Remote Config parameters
        return MetadataResponse(
            status = "ok",
            appVersion = "1.0.0",
            promptVersion = "1.0.0",
            exerciseVersion = "1.0.0"
        )
    }
}
