package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.canvas.domain.RemoveExerciseFromScribbleUseCase
import com.scribblefit.feature.canvas.domain.UpdateScribbleExerciseUseCase
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.usecase.AddExercisesUseCase
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal object CanvasModule {
    @Provides
    fun provideParsePendingScribblesUseCase(
        getPendingScribblesByDateUseCase: GetPendingScribblesByDateUseCase,
        updateScribbleUseCase: UpdateScribbleUseCase,
        addExercisesUseCase: AddExercisesUseCase,
        llmEngine: LLMEngine,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ParsePendingScribblesUseCase =
        ParsePendingScribblesUseCase(
            getPendingScribblesByDateUseCase = getPendingScribblesByDateUseCase,
            updateScribbleUseCase = updateScribbleUseCase,
            addExercisesUseCase = addExercisesUseCase,
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

    @Provides
    fun provideUpdateScribbleExerciseUseCase(
        updateExerciseUseCase: UpdateExerciseUseCase,
        removeSetUseCase: RemoveSetUseCase,
    ): UpdateScribbleExerciseUseCase =
        UpdateScribbleExerciseUseCase(
            updateExerciseUseCase = updateExerciseUseCase,
            removeSetUseCase = removeSetUseCase,
        )
}
