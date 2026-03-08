package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.entity.SystemConfigEntity
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.model.SystemConfig
import com.scribblefit.feature.profile.domain.model.AppSettings
import com.scribblefit.feature.profile.domain.model.ParsingMode
import com.scribblefit.feature.profile.domain.model.ThemePreference
import com.scribblefit.feature.profile.domain.model.WeightUnit
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val systemConfigDao: SystemConfigDao
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> =
        systemConfigDao.observe().map { entity ->
            entity?.toAppSettings() ?: AppSettings()
        }

    override suspend fun updateSettings(settings: AppSettings) {
        val existing = systemConfigDao.get()
        val entity = SystemConfigEntity(
            id = "config",
            promptVersion = existing?.promptVersion ?: "1.0.0",
            promptText = existing?.promptText ?: SystemConfig.defaultPrompt,
            exerciseVersion = existing?.exerciseVersion ?: "0.0.0",
            preferredLlmProvider = settings.aiProvider.rawValue,
            preferredModel = settings.selectedModel,
            parsingMode = settings.parsingMode.name.lowercase(),
            weightUnit = settings.weightUnit.name.lowercase(),
            themePreference = settings.themePreference.name.lowercase(),
            updatedAt = System.currentTimeMillis()
        )
        systemConfigDao.upsert(entity)
    }

    override suspend fun clearAllData() {
        systemConfigDao.deleteAll()
    }

    private fun SystemConfigEntity.toAppSettings(): AppSettings = AppSettings(
        parsingMode = if (parsingMode == "personal") ParsingMode.PERSONAL else ParsingMode.CLOUD,
        aiProvider = LLMProvider.entries.find { it.rawValue == preferredLlmProvider } ?: LLMProvider.PROXY,
        weightUnit = if (weightUnit == "kg") WeightUnit.KG else WeightUnit.LBS,
        themePreference = ThemePreference.entries.find {
            it.name.equals(themePreference, ignoreCase = true)
        } ?: ThemePreference.SYSTEM,
        selectedModel = preferredModel
    )
}
