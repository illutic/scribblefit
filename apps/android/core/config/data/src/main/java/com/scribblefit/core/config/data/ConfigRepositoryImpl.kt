package com.scribblefit.core.config.data

import com.scribblefit.core.config.data.datasource.FirebaseRemoteConfigDataSource
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SystemConfig
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.entity.config.toDomain
import com.scribblefit.core.database.entity.config.toEntity
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn

class ConfigRepositoryImpl(
    private val systemConfigDao: SystemConfigDao,
    private val remoteConfigDataSource: FirebaseRemoteConfigDataSource,
    dispatcherProvider: CoroutineDispatcherProvider
) : ConfigRepository,
    CoroutineScope by CoroutineScope(dispatcherProvider.default() + CoroutineName("ConfigRepository")) {
    private val defaultConfig = SystemConfig()

    override val config = combine(
        systemConfigDao.getSystemConfig().mapNotNull { it?.toDomain() },
        remoteConfigDataSource.getRemoteConfig()
    ) { localConfig, remoteConfig ->
        localConfig.copy(remoteConfig = remoteConfig)
    }.stateIn(this, SharingStarted.Eagerly, defaultConfig)

    override suspend fun updateConfig(config: SystemConfig) {
        systemConfigDao.insertSystemConfig(config.toEntity())
    }

    override suspend fun resetConfig() {
        systemConfigDao.insertSystemConfig(defaultConfig.toEntity())
    }
}