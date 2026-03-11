package com.scribblefit.feature.canvas.data

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.feature.canvas.domain.usecase.ConfirmWorkoutUseCase
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CanvasModule {
    @Provides
    @Singleton
    fun provideProcessScribbleUseCase(
        scribbleRepository: ScribbleRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ProcessScribbleUseCase =
        ProcessScribbleUseCase(
            scribbleRepository = scribbleRepository,
            coroutineDispatcher = coroutineDispatcherProvider.default()
        )

    @Provides
    @Singleton
    fun provideConfirmWorkoutUseCase(
        scribbleRepository: ScribbleRepository,
        ledgerRepository: LedgerRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): ConfirmWorkoutUseCase = ConfirmWorkoutUseCase(
        scribbleRepository, ledgerRepository,
        coroutineDispatcher = coroutineDispatcherProvider.default()
    )
}
