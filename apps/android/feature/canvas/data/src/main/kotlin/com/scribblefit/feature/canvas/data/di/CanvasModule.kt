package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.domain.LLMEngineProxy
import com.scribblefit.feature.canvas.domain.*
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.canvas.domain.GetScribblesForDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import com.scribblefit.feature.workouts.domain.usecase.InsertWorkoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object CanvasModule {
    @Provides
    fun provideParsePendingScribblesUseCase(
        getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
        updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase,
        updateScribbleAsFailedUseCase: UpdateScribbleAsFailedUseCase,
        updateScribbleAsPendingUseCase: UpdateScribbleAsPendingUseCase,
        llmEngine: LLMEngineProxy,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ParsePendingScribblesUseCase =
        ParsePendingScribblesUseCase(
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleWithWorkoutUseCase = updateScribbleWithWorkoutUseCase,
            updateScribbleAsFailedUseCase = updateScribbleAsFailedUseCase,
            updateScribbleAsPendingUseCase = updateScribbleAsPendingUseCase,
            llmEngineProxy = llmEngine,
            coroutineDispatcher = coroutineDispatcherProvider.io(),
        )

    @Provides
    fun provideGetScribblesForDateUseCase(
        repository: ScribbleRepository
    ): GetScribblesForDateUseCase = GetScribblesForDateUseCase(repository)

    @Provides
    fun provideAddScribbleUseCase(
        repository: ScribbleRepository
    ): AddScribbleUseCase = AddScribbleUseCase(repository)

    @Provides
    fun provideConfirmScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        insertWorkoutUseCase: InsertWorkoutUseCase,
    ): ConfirmScribbleUseCase = ConfirmScribbleUseCase(scribbleRepository, insertWorkoutUseCase)

    @Provides
    fun provideDeleteScribbleUseCase(
        removeScribbleUseCase: RemoveScribbleUseCase
    ): DeleteScribbleUseCase = DeleteScribbleUseCase(removeScribbleUseCase)
}
