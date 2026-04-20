package com.scribblefit.core.config.data

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.mapper.toDomain
import com.scribblefit.core.database.mapper.toEntity
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.time.Clock

class ConfigRepositoryImpl @Inject constructor(
    private val systemConfigDao: SystemConfigDao,
    dispatcherProvider: CoroutineDispatcherProvider
) : ConfigRepository,
    CoroutineScope by CoroutineScope(dispatcherProvider.default() + CoroutineName("ConfigRepository")) {
    private val defaultConfig = SystemConfig(
        summaryPrompt = SystemConfig.SUMMARY_PROMPT,
        suggestionPrompt = SystemConfig.SUGGESTION_PROMPT,
        insightPrompt = SystemConfig.INSIGHT_PROMPT,
        parsePrompt = SystemConfig.PARSE_PROMPT,
        preferredLlmProvider = LLMProvider.LOCAL,
        weightUnit = Weight.KGS,
        themePreference = ThemePreference.SYSTEM,
        isDynamicTheme = false,
        updatedAt = Clock.System.now().toEpochMilliseconds()
    )

    override val config = systemConfigDao.getSystemConfig()
        .mapNotNull { it?.toDomain() }
        .stateIn(this, SharingStarted.Eagerly, defaultConfig)

    override suspend fun updateConfig(config: SystemConfig) {
        systemConfigDao.insertSystem(config.toEntity())
    }

    override suspend fun resetConfig() {
        systemConfigDao.insertSystem(defaultConfig.toEntity())
    }
}