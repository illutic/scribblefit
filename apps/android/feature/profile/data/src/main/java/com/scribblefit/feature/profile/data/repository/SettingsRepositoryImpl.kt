package com.scribblefit.feature.profile.data.repository

import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.model.SystemConfigEntity
import com.scribblefit.feature.profile.domain.model.*
import com.scribblefit.feature.profile.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val systemConfigDao: SystemConfigDao,
    private val database: ScribbleFitDatabase
) : SettingsRepository {

    override fun getSettings(): Flow<AppSettings> {
        return systemConfigDao.getConfig().map { entity ->
            AppSettings(
                parsingMode = ParsingMode.valueOf(entity?.parsingMode ?: "CLOUD"),
                aiProvider = entity?.preferredLlmProvider ?: com.scribblefit.feature.ai.domain.model.LLMProvider.PROXY,
                weightUnit = WeightUnit.valueOf(entity?.weightUnit ?: "LBS"),
                themePreference = ThemePreference.valueOf(entity?.themePreference ?: "SYSTEM")
            )
        }
    }

    override suspend fun updateSettings(settings: AppSettings) {
        val newEntity = SystemConfigEntity(
            promptVersion = "1.0.0",
            promptText = "",
            preferredLlmProvider = settings.aiProvider,
            parsingMode = settings.parsingMode.name,
            weightUnit = settings.weightUnit.name,
            themePreference = settings.themePreference.name,
            updatedAt = System.currentTimeMillis()
        )
        systemConfigDao.upsertConfig(newEntity)
    }

    override suspend fun clearAllData() {
        database.clearAllTables()
    }
}
