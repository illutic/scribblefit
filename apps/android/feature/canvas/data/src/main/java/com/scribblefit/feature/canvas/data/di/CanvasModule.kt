package com.scribblefit.feature.canvas.data.di

import com.scribblefit.feature.canvas.data.repository.CanvasRepositoryImpl
import com.scribblefit.feature.canvas.data.repository.WorkoutSessionRepositoryImpl
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.repository.WorkoutSessionRepository
import com.scribblefit.feature.canvas.domain.usecase.ConfirmWorkoutUseCase
import com.scribblefit.feature.canvas.domain.usecase.ExecuteQuickActionUseCase
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CanvasModule {

    @Binds @Singleton
    abstract fun bindCanvasRepository(impl: CanvasRepositoryImpl): CanvasRepository

    @Binds @Singleton
    abstract fun bindWorkoutSessionRepository(impl: WorkoutSessionRepositoryImpl): WorkoutSessionRepository

    companion object {
        @Provides @Singleton
        fun provideProcessScribbleUseCase(canvasRepository: CanvasRepository): ProcessScribbleUseCase =
            ProcessScribbleUseCase(canvasRepository)

        @Provides @Singleton
        fun provideConfirmWorkoutUseCase(
            canvasRepository: CanvasRepository,
            sessionRepository: WorkoutSessionRepository,
            ledgerRepository: LedgerRepository
        ): ConfirmWorkoutUseCase = ConfirmWorkoutUseCase(canvasRepository, sessionRepository, ledgerRepository)

        @Provides @Singleton
        fun provideExecuteQuickActionUseCase(canvasRepository: CanvasRepository): ExecuteQuickActionUseCase =
            ExecuteQuickActionUseCase(canvasRepository)
    }
}
