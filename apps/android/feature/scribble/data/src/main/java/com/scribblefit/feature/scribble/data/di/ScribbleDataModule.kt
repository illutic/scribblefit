package com.scribblefit.feature.scribble.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.scribble.data.ScribbleRepositoryImpl
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.usecase.AddScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ConfirmScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.CreateManualScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesForDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to bind ScribbleRepository implementation and provide use cases.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ScribbleDataModule {

    @Provides
    @Singleton
    fun provideScribbleRepository(
        database: ScribbleFitDatabase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ScribbleRepository = ScribbleRepositoryImpl(
        scribbleDao = database.scribbleDao(),
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetScribblesForDateUseCase(
        repository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetScribblesForDateUseCase =
        GetScribblesForDateUseCase(repository, coroutineDispatcherProvider.default())

    @Provides
    fun provideAddScribbleUseCase(
        repository: ScribbleRepository
    ): AddScribbleUseCase = AddScribbleUseCase(repository)

    @Provides
    fun provideConfirmScribbleUseCase(
        repository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ConfirmScribbleUseCase = ConfirmScribbleUseCase(
        scribbleRepository = repository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetPendingScribblesByDateUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetPendingScribblesByDateUseCase = GetPendingScribblesByDateUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideRemoveScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): RemoveScribbleUseCase = RemoveScribbleUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateScribbleUseCase = UpdateScribbleUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideCreateManualScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): CreateManualScribbleUseCase =
        CreateManualScribbleUseCase(
            scribbleRepository = scribbleRepository,
            coroutineDispatcher = coroutineDispatcherProvider.default()
        )
}
