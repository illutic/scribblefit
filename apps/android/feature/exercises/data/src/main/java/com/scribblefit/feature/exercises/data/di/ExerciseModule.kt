package com.scribblefit.feature.exercises.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.exercises.data.ExerciseRepositoryImpl
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import com.scribblefit.feature.exercises.domain.usecase.AddExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateTrendsUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateWeeklyStatsUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseAIInsightUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseByIdUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExercisesInRangeUseCase
import com.scribblefit.feature.exercises.domain.usecase.MarkExerciseAsCompleteUseCase
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.sets.domain.SetRepository
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
        database: ScribbleFitDatabase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ExerciseRepository = ExerciseRepositoryImpl(
        exerciseDao = database.exerciseDao(),
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )

    @Provides
    fun provideRemoveExerciseUseCase(
        repository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): RemoveExerciseUseCase = RemoveExerciseUseCase(
        repository = repository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateExerciseUseCase(
        repository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateExerciseUseCase = UpdateExerciseUseCase(
        repository = repository,
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
    fun provideGetExerciseAIInsightUseCase(
        exerciseRepository: ExerciseRepository,
        llmEngine: LLMEngine
    ): GetExerciseAIInsightUseCase = GetExerciseAIInsightUseCase(
        llmEngine = llmEngine,
        exerciseRepository = exerciseRepository
    )

    @Provides
    fun provideFormatExerciseSummaryUseCase(): FormatExerciseSummaryUseCase =
        FormatExerciseSummaryUseCase()

    @Provides
    fun provideAddManualExerciseUseCase(
        exerciseRepository: ExerciseRepository,
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): AddExerciseUseCase =
        AddExerciseUseCase(
            exerciseRepository = exerciseRepository,
            setRepository = setRepository,
            coroutineDispatcher = coroutineDispatcherProvider.default()
        )

    @Provides
    fun provideCalculateWeeklyStatsUseCase(
        exerciseRepository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): CalculateWeeklyStatsUseCase = CalculateWeeklyStatsUseCase(
        exerciseRepository = exerciseRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideCalculateTrendsUseCase(
        exerciseRepository: ExerciseRepository,
        calculateWeeklyStatsUseCase: CalculateWeeklyStatsUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): CalculateTrendsUseCase = CalculateTrendsUseCase(
        exerciseRepository = exerciseRepository,
        calculateWeeklyStatsUseCase = calculateWeeklyStatsUseCase,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetExerciseByIdUseCase(
        exerciseRepository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetExerciseByIdUseCase = GetExerciseByIdUseCase(
        exerciseRepository = exerciseRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetExercisesInRangeUseCase(
        exerciseRepository: ExerciseRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetExercisesInRangeUseCase = GetExercisesInRangeUseCase(
        exerciseRepository = exerciseRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )
}
