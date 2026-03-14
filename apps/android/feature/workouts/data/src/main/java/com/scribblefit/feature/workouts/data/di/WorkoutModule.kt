package com.scribblefit.feature.workouts.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.workouts.data.WorkoutRepositoryImpl
import com.scribblefit.feature.workouts.domain.WorkoutRepository
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
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )
}
