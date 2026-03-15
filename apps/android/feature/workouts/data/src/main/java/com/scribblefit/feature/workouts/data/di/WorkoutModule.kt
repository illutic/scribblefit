package com.scribblefit.feature.workouts.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.exercises.domain.usecase.InsertExerciseToWorkoutUseCase
import com.scribblefit.feature.workouts.data.WorkoutRepositoryImpl
import com.scribblefit.feature.workouts.domain.WorkoutRepository
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutByDateUseCase
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutUseCase
import com.scribblefit.feature.workouts.domain.usecase.InsertWorkoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object WorkoutModule {

    @Provides
    @Singleton
    fun provideWorkoutRepository(
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
        workoutDao: WorkoutDao
    ): WorkoutRepository = WorkoutRepositoryImpl(
        workoutDao = workoutDao,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetWorkoutByDateUseCase(
        workoutRepository: WorkoutRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetWorkoutByDateUseCase = GetWorkoutByDateUseCase(
        repository = workoutRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetWorkoutUseCase(
        workoutRepository: WorkoutRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetWorkoutUseCase = GetWorkoutUseCase(
        repository = workoutRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideInsertWorkoutUseCase(
        workoutRepository: WorkoutRepository,
        insertExerciseToWorkoutUseCase: InsertExerciseToWorkoutUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): InsertWorkoutUseCase = InsertWorkoutUseCase(
        repository = workoutRepository,
        insertExerciseToWorkoutUseCase = insertExerciseToWorkoutUseCase,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )
}
