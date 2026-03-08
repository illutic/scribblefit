package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.model.DEFAULT_PROMPT
import com.scribblefit.feature.ai.domain.model.SystemConfig
import com.scribblefit.feature.profile.domain.model.*
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val configRepository: ConfigRepository,
    private val database: ScribbleFitDatabase
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return configRepository.getConfig().map { config ->
            AppSettings(
                parsingMode = ParsingMode.valueOf(config?.parsingMode ?: "CLOUD"),
                aiProvider = config?.preferredLlmProvider ?: com.scribblefit.feature.ai.domain.model.LLMProvider.PROXY,
                weightUnit = WeightUnit.valueOf(config?.weightUnit ?: "LBS"),
                themePreference = ThemePreference.valueOf(config?.themePreference ?: "SYSTEM"),
                selectedModel = config?.preferredModel ?: ""
            )
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        val currentConfig = configRepository.getConfig().first()
        val newConfig = (currentConfig ?: SystemConfig(
            promptVersion = "1.0.0",
            promptText = DEFAULT_PROMPT,
            exerciseVersion = "0.0.0",
            preferredLlmProvider = settings.aiProvider,
            preferredModel = settings.selectedModel,
            parsingMode = settings.parsingMode.name,
            weightUnit = settings.weightUnit.name,
            themePreference = settings.themePreference.name,
            updatedAt = System.currentTimeMillis()
        )).copy(
            preferredLlmProvider = settings.aiProvider,
            preferredModel = settings.selectedModel,
            parsingMode = settings.parsingMode.name,
            weightUnit = settings.weightUnit.name,
            themePreference = settings.themePreference.name,
            updatedAt = System.currentTimeMillis()
        )
        configRepository.updateConfig(newConfig)
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
    }
}
