package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.canvas.domain.AddScribbleUseCase
import com.scribblefit.feature.canvas.domain.ConfirmScribbleUseCase
import com.scribblefit.feature.canvas.domain.DeleteScribbleUseCase
import com.scribblefit.feature.canvas.domain.GetScribblesForDateUseCase
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object CanvasModule {
    @Provides
    @Singleton
    fun provideParsePendingScribblesUseCase(
        getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
        updateScribbleWithWorkoutUseCase: UpdateScribbleWithWorkoutUseCase,
        updateScribbleAsFailedUseCase: UpdateScribbleAsFailedUseCase,
        updateScribbleAsPendingUseCase: UpdateScribbleAsPendingUseCase,
        llmEngine: LLMEngine,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ParsePendingScribblesUseCase =
        ParsePendingScribblesUseCase(
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleWithWorkoutUseCase = updateScribbleWithWorkoutUseCase,
            updateScribbleAsFailedUseCase = updateScribbleAsFailedUseCase,
            updateScribbleAsPendingUseCase = updateScribbleAsPendingUseCase,
            llmEngine = llmEngine,
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
        workoutRepository: com.scribblefit.feature.workouts.domain.WorkoutRepository,
    ): ConfirmScribbleUseCase =
        ConfirmScribbleUseCase(scribbleRepository, workoutRepository)

    @Provides
    fun provideDeleteScribbleUseCase(
        removeScribbleUseCase: RemoveScribbleUseCase
    ): DeleteScribbleUseCase = DeleteScribbleUseCase(removeScribbleUseCase)
}
