package com.scribblefit.feature.sets.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutExerciseDao
import com.scribblefit.feature.sets.data.SetRepositoryImpl
import com.scribblefit.feature.sets.domain.SetRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SetModule {

    @Provides
    @Singleton
    fun providesSetRepository(
        workoutExerciseDao: WorkoutExerciseDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): SetRepository = SetRepositoryImpl(
        workoutExerciseDao = workoutExerciseDao,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )
}
