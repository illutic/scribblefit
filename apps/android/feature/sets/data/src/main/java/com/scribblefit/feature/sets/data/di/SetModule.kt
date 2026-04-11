package com.scribblefit.feature.sets.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutExerciseDao
import com.scribblefit.feature.sets.data.SetRepositoryImpl
import com.scribblefit.feature.sets.domain.SetRepository
import com.scribblefit.feature.sets.domain.usecase.GetSetsForExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.InsertSetToExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase
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

    @Provides
    fun provideInsertSetToExerciseUseCase(
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): InsertSetToExerciseUseCase = InsertSetToExerciseUseCase(
        repository = setRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetSetsForExerciseUseCase(
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetSetsForExerciseUseCase = GetSetsForExerciseUseCase(
        repository = setRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideRemoveSetUseCase(
        setRepository: SetRepository
    ): RemoveSetUseCase = RemoveSetUseCase(
        setRepository = setRepository
    )
}
