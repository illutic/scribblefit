package com.scribblefit.api.features.config

import com.scribblefit.api.features.exercises.ExerciseService
import io.ktor.server.config.*

class ConfigServiceImpl(
    config: ApplicationConfig,
    private val exerciseService: ExerciseService
) : ConfigService {
    private val appVersion = config.propertyOrNull("scribblefit.version")?.getString() ?: "1.0.0"
    private val promptVersion = config.propertyOrNull("scribblefit.config.promptVersion")?.getString() ?: "1.0.0"
    private val promptText = config.propertyOrNull("scribblefit.config.promptText")?.getString() ?: ""

    override fun getPromptConfig(): ConfigResponse {
        return ConfigResponse(
            version = promptVersion,
            prompt = promptText
        )
    }

    override fun getMetadata(): MetadataResponse {
        return MetadataResponse(
            status = "ok",
            appVersion = appVersion,
            promptVersion = promptVersion,
            exerciseVersion = exerciseService.getExerciseVersion()
        )
    }
}
