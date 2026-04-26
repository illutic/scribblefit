package com.scribblefit.core.config.data

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfig
import com.scribblefit.core.config.data.datasource.FirebaseRemoteConfigDataSource
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
    fun provideFirebaseRemoteConfig(): FirebaseRemoteConfig = Firebase.remoteConfig

    @Singleton
    @Provides
    fun provideRemoteConfigDataSource(
        remoteConfig: FirebaseRemoteConfig
    ): FirebaseRemoteConfigDataSource = FirebaseRemoteConfigDataSource(remoteConfig)

    @Singleton
    @Provides
    fun provideConfigRepository(
        database: ScribbleFitDatabase,
        remoteConfigDataSource: FirebaseRemoteConfigDataSource,
        dispatcherProvider: CoroutineDispatcherProvider
    ): ConfigRepository = ConfigRepositoryImpl(
        systemConfigDao = database.systemConfigDao(),
        remoteConfigDataSource = remoteConfigDataSource,
        dispatcherProvider = dispatcherProvider
    )
}
