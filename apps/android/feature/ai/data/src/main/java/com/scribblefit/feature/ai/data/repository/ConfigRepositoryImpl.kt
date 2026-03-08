package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.entity.SystemConfigEntity
import com.scribblefit.feature.ai.domain.engine.ConfigRepository
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.ai.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigRepositoryImpl @Inject constructor(
    private val systemConfigDao: SystemConfigDao
) : ConfigRepository {

    override fun getConfig(): Flow<SystemConfig?> =
        systemConfigDao.observe().map { it?.toDomain() }

    override suspend fun updateConfig(config: SystemConfig) {
        systemConfigDao.upsert(config.toEntity())
    }

    private fun SystemConfigEntity.toDomain(): SystemConfig = SystemConfig(
        promptVersion = promptVersion,
        promptText = promptText,
        exerciseVersion = exerciseVersion,
        preferredLlmProvider = LLMProvider.entries.find { it.rawValue == preferredLlmProvider } ?: LLMProvider.PROXY,
        preferredModel = preferredModel,
        parsingMode = parsingMode,
        weightUnit = weightUnit,
        themePreference = themePreference,
        updatedAt = updatedAt
    )

    private fun SystemConfig.toEntity(): SystemConfigEntity = SystemConfigEntity(
        id = "config",
        promptVersion = promptVersion,
        promptText = promptText,
        exerciseVersion = exerciseVersion,
        preferredLlmProvider = preferredLlmProvider.rawValue,
        preferredModel = preferredModel,
        parsingMode = parsingMode,
        weightUnit = weightUnit,
        themePreference = themePreference,
        updatedAt = updatedAt
    )
}
