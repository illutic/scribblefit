package com.scribblefit.feature.scribble.data

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.SyncScribblesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ScribbleModule {
    @Provides
    @Singleton
    fun provideScribbleRepository(
        scribbleDao: ScribbleDao,
        setDao: SetDao,
        exerciseDao: ExerciseDao,
        workoutDao: WorkoutDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider,
    ): ScribbleRepository = ScribbleRepositoryImpl(
        scribbleDao = scribbleDao,
        setDao = setDao,
        exerciseDao = exerciseDao,
        workoutDao = workoutDao,
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )

    @Provides
    @Singleton
    fun provideSyncWorkoutUseCase(
        scribbleRepository: ScribbleRepository,
        llmEngine: LLMEngine,
        dispatcherProvider: CoroutineDispatcherProvider
    ): SyncScribblesUseCase =
        SyncScribblesUseCase(
            scribbleRepository = scribbleRepository,
            engine = llmEngine,
            coroutineDispatcher = dispatcherProvider.default()
        )
}
