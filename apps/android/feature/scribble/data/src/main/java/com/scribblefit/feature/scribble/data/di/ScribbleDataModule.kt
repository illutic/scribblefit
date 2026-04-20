package com.scribblefit.feature.scribble.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.ScribbleTrackerDao
import com.scribblefit.feature.exercises.domain.usecase.MarkExerciseAsCompleteUseCase
import com.scribblefit.feature.scribble.data.ScribbleRepositoryImpl
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.usecase.AddRawScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.EditScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetPendingScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesByDateUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsCompleteUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsFailedUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleAsPendingUseCase
import com.scribblefit.feature.scribble.domain.usecase.UpdateScribbleWithWorkoutUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module to bind ScribbleRepository implementation and provide use cases.
 */
@Module
@InstallIn(SingletonComponent::class)
internal object ScribbleDataModule {

    @Provides
    @Singleton
    fun provideScribbleRepository(
        scribbleDao: ScribbleDao,
        scribbleTrackerDao: ScribbleTrackerDao,
        workoutDao: com.scribblefit.core.database.dao.WorkoutDao,
        workoutExerciseDao: com.scribblefit.core.database.dao.WorkoutExerciseDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ScribbleRepository = ScribbleRepositoryImpl(
        scribbleDao = scribbleDao,
        scribbleTrackerDao = scribbleTrackerDao,
        workoutDao = workoutDao,
        workoutExerciseDao = workoutExerciseDao,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )

    @Provides
    fun provideAddRawScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): AddRawScribbleUseCase = AddRawScribbleUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideEditScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): EditScribbleUseCase = EditScribbleUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetPendingScribblesByDateUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetPendingScribblesByDateUseCase = GetPendingScribblesByDateUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideGetScribblesByDateUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetScribblesByDateUseCase = GetScribblesByDateUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideRemoveScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): RemoveScribbleUseCase = RemoveScribbleUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateScribbleAsCompleteUseCase(
        scribbleRepository: ScribbleRepository,
        markExerciseAsCompleteUseCase: MarkExerciseAsCompleteUseCase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateScribbleAsCompleteUseCase = UpdateScribbleAsCompleteUseCase(
        scribbleRepository = scribbleRepository,
        markExerciseAsCompleteUseCase = markExerciseAsCompleteUseCase,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateScribbleWithWorkoutUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateScribbleWithWorkoutUseCase = UpdateScribbleWithWorkoutUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateScribbleAsFailedUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateScribbleAsFailedUseCase = UpdateScribbleAsFailedUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateScribbleAsPendingUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateScribbleAsPendingUseCase = UpdateScribbleAsPendingUseCase(
        scribbleRepository = scribbleRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )
}
