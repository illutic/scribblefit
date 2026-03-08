package com.scribblefit.feature.canvas.data.di

import com.scribblefit.feature.canvas.data.repository.CanvasRepositoryImpl
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
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

    @Binds
    @Singleton
    abstract fun bindCanvasRepository(impl: CanvasRepositoryImpl): CanvasRepository

    companion object {
        @Provides
        @Singleton
        fun provideProcessScribbleUseCase(repository: CanvasRepository): ProcessScribbleUseCase {
            return ProcessScribbleUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideExecuteQuickActionUseCase(repository: CanvasRepository): ExecuteQuickActionUseCase {
            return ExecuteQuickActionUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideConfirmWorkoutUseCase(
            ledgerRepository: LedgerRepository
        ): ConfirmWorkoutUseCase {
            return ConfirmWorkoutUseCase(ledgerRepository)
        }
    }
}
