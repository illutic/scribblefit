package com.scribblefit.core.config.data

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.SystemConfigDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ConfigModule {
    @Provides
    @Singleton
    fun provideConfigRepository(
        systemConfigDao: SystemConfigDao,
        configDispatcherProvider: CoroutineDispatcherProvider
    ): ConfigRepository = ConfigRepositoryImpl(
        systemConfigDao = systemConfigDao,
        coroutineDispatcher = configDispatcherProvider.io()
    )
}