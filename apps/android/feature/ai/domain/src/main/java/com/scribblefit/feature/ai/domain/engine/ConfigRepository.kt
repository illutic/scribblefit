package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.SystemConfig
import kotlinx.coroutines.flow.Flow

interface ConfigRepository {
    fun getConfig(): Flow<SystemConfig?>
    suspend fun updateConfig(config: SystemConfig)
    suspend fun syncMetadata(): Result<Unit>
}
