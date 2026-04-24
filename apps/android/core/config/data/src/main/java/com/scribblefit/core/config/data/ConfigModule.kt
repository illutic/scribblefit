package com.scribblefit.core.config.data

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.ScribbleFitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ConfigModule {

    @Singleton
    @Provides
    fun provideConfigRepository(
        database: ScribbleFitDatabase,
        // remoteConfigDataSource: RemoteConfigDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): ConfigRepository = ConfigRepositoryImpl(
        systemConfigDao = database.systemConfigDao(),
        // remoteConfigDataSource = remoteConfigDataSource,
        dispatcherProvider = dispatcherProvider
    )
}
