package com.scribblefit.api.services

import com.scribblefit.api.models.ConfigResponse
import io.ktor.server.config.*

class ConfigServiceImpl(config: ApplicationConfig) : ConfigService {
    private val version = config.propertyOrNull("scribblefit.config.promptVersion")?.getString() ?: "unknown"
    private val prompt = config.propertyOrNull("scribblefit.config.promptText")?.getString() ?: ""

    override fun getPromptConfig(): ConfigResponse {
        return ConfigResponse(
            version = version,
            prompt = prompt
        )
    }
}
