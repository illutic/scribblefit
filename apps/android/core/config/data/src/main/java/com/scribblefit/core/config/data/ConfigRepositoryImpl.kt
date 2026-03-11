package com.scribblefit.core.config.data

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.entity.SystemConfigEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Clock

class ConfigRepositoryImpl(
    private val systemConfigDao: SystemConfigDao,
    coroutineDispatcher: CoroutineDispatcher
) : ConfigRepository,
    CoroutineScope by CoroutineScope(coroutineDispatcher + CoroutineName("ConfigRepository")) {
    private val defaultConfig = SystemConfig(
        summaryPrompt = SystemConfig.SUMMARY_PROMPT,
        suggestionPrompt = SystemConfig.SUGGESTION_PROMPT,
        insightPrompt = SystemConfig.INSIGHT_PROMPT,
        parsePrompt = SystemConfig.PARSE_PROMPT,
        preferredLlmProvider = LLMProvider.LOCAL,
        preferredModel = null,
        weightUnit = Weight.KGS,
        themePreference = ThemePreference.SYSTEM,
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )

    override val config = systemConfigDao.observe()
        .mapNotNull { it?.toDomain() }
        .stateIn(this, SharingStarted.Eagerly, defaultConfig)

    override suspend fun updateConfig(config: SystemConfig) {
        systemConfigDao.upsert(config.toEntity())
    }

    override suspend fun resetConfig() {
        systemConfigDao.upsert(defaultConfig.toEntity())
    }

    private fun SystemConfigEntity.toDomain(): SystemConfig = SystemConfig(
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        preferredLlmProvider = LLMProvider.valueOf(preferredLlmProvider.uppercase()),
        preferredModel = preferredModel,
        weightUnit = Weight.valueOf(weightUnit.uppercase()),
        themePreference = ThemePreference.valueOf(themePreference.uppercase()),
        updatedAt = updatedAt
    )

    private fun SystemConfig.toEntity(): SystemConfigEntity = SystemConfigEntity(
        id = "config",
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        preferredLlmProvider = preferredLlmProvider.name,
        preferredModel = preferredModel,
        weightUnit = weightUnit.name,
        themePreference = themePreference.name,
        updatedAt = updatedAt
    )
}