package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.canvas.domain.RemoveExerciseFromScribbleUseCase
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.usecase.AddExerciseUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleUseCase
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
        updateScribbleUseCase: UpdateScribbleUseCase,
        addExerciseUseCase: AddExerciseUseCase,
        llmEngine: LLMEngine,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ParsePendingScribblesUseCase =
        ParsePendingScribblesUseCase(
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleUseCase = updateScribbleUseCase,
            addExerciseUseCase = addExerciseUseCase,
            llmEngine = llmEngine,
            coroutineDispatcher = coroutineDispatcherProvider.io(),
        )

    @Provides
    fun provideRemoveExerciseFromScribbleUseCase(
        exerciseRepository: ExerciseRepository,
        removeScribbleUseCase: RemoveScribbleUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): RemoveExerciseFromScribbleUseCase =
        RemoveExerciseFromScribbleUseCase(
            exerciseRepository = exerciseRepository,
            removeScribbleUseCase = removeScribbleUseCase,
            coroutineDispatcher = coroutineDispatcherProvider.default(),
        )
}
