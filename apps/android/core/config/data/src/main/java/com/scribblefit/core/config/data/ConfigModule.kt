package com.scribblefit.core.config.data

import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.SecureKeyStorage
import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.SystemConfigDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface ConfigModule {
    @Binds
    @Singleton
    fun bindSecureKeyStorage(impl: SecureKeyStorageImpl): SecureKeyStorage


    companion object {
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
}
