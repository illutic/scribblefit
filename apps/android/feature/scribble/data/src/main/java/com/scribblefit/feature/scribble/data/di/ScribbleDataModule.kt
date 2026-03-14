package com.scribblefit.feature.scribble.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.feature.scribble.data.ScribbleRepositoryImpl
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to bind ScribbleRepository implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ScribbleDataModule {

    @Provides
    @Singleton
    fun provideScribbleRepository(
        scribbleDao: ScribbleDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ScribbleRepository = ScribbleRepositoryImpl(
        scribbleDao = scribbleDao,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )
}
