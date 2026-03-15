package com.scribblefit.feature.exercises.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.feature.exercises.data.ExerciseRepositoryImpl
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

import com.scribblefit.core.database.dao.WorkoutExerciseDao

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
}
