package com.scribblefit.core.config.domain

import kotlinx.coroutines.flow.StateFlow

interface ConfigRepository {
    val config: StateFlow<SystemConfig>
    suspend fun updateConfig(config: SystemConfig)
    suspend fun resetConfig()
}