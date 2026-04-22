package com.scribblefit.feature.exercises.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.WorkoutExerciseDao
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.exercises.data.ExerciseRepositoryImpl
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseAIInsightUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseDetailsUseCase
import com.scribblefit.feature.exercises.domain.usecase.InsertExerciseToWorkoutUseCase
import com.scribblefit.feature.exercises.domain.usecase.MarkExerciseAsCompleteUseCase
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.InsertSetToExerciseUseCase
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ExerciseModule {

    @Provides
    @Singleton
    fun providesExerciseRepository(
        exerciseDao: ExerciseDao,
        workoutExerciseDao: WorkoutExerciseDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ExerciseRepository = ExerciseRepositoryImpl(
        exerciseDao = exerciseDao,
        workoutExerciseDao = workoutExerciseDao,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )

    @Provides
    fun provideInsertExerciseToWorkoutUseCase(
        exerciseRepository: ExerciseRepository,
        insertSetToExerciseUseCase: InsertSetToExerciseUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): InsertExerciseToWorkoutUseCase = InsertExerciseToWorkoutUseCase(
        repository = exerciseRepository,
        insertSetToExerciseUseCase = insertSetToExerciseUseCase,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideRemoveExerciseUseCase(
        exerciseRepository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): RemoveExerciseUseCase = RemoveExerciseUseCase(
        repository = exerciseRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateExerciseUseCase(
        exerciseRepository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateExerciseUseCase = UpdateExerciseUseCase(
        repository = exerciseRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideMarkExerciseAsCompleteUseCase(
        updateExerciseUseCase: UpdateExerciseUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): MarkExerciseAsCompleteUseCase = MarkExerciseAsCompleteUseCase(
        updateExerciseUseCase = updateExerciseUseCase,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetExerciseDetailsUseCase(
        workoutRepository: WorkoutRepository
    ): GetExerciseDetailsUseCase = GetExerciseDetailsUseCase(
        workoutRepository = workoutRepository
    )

    @Provides
    fun provideGetExerciseAIInsightUseCase(
        llmEngine: LLMEngine
    ): GetExerciseAIInsightUseCase = GetExerciseAIInsightUseCase(
        llmEngine = llmEngine
    )

    @Provides
    fun provideFormatExerciseSummaryUseCase(): FormatExerciseSummaryUseCase =
        FormatExerciseSummaryUseCase()
}
