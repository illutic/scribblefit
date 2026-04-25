package com.scribblefit.feature.sets.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.sets.data.SetRepositoryImpl
import com.scribblefit.feature.sets.domain.SetRepository
import com.scribblefit.feature.sets.domain.usecase.AddSetToExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase
import com.scribblefit.feature.sets.domain.usecase.ReorderSetsUseCase
import com.scribblefit.feature.sets.domain.usecase.UpdateSetRepsUseCase
import com.scribblefit.feature.sets.domain.usecase.UpdateSetWeightUseCase
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
        database: ScribbleFitDatabase,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): SetRepository = SetRepositoryImpl(
        setDao = database.setDao(),
        coroutineDispatcher = coroutineDispatcherProvider.io()
    )

    @Provides
    fun provideRemoveSetUseCase(
        setRepository: SetRepository
    ): RemoveSetUseCase = RemoveSetUseCase(
        setRepository = setRepository
    )

    @Provides
    fun provideUpdateSetRepsUseCase(
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateSetRepsUseCase = UpdateSetRepsUseCase(
        repository = setRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideUpdateSetWeightUseCase(
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): UpdateSetWeightUseCase = UpdateSetWeightUseCase(
        repository = setRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )

    @Provides
    fun provideReorderSetsUseCase(): ReorderSetsUseCase = ReorderSetsUseCase()

    @Provides
    fun provideAddSetToExerciseUseCase(
        setRepository: SetRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): AddSetToExerciseUseCase =
        AddSetToExerciseUseCase(
            repository = setRepository,
            coroutineDispatcher = coroutineDispatcherProvider.default()
        )
}
