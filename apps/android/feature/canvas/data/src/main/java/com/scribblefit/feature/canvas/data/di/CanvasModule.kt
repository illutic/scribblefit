package com.scribblefit.feature.canvas.data.di

import com.scribblefit.core.database.dao.CanvasFeedDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.ActiveSessionDao
import com.scribblefit.feature.canvas.data.repository.CanvasRepositoryImpl
import com.scribblefit.feature.canvas.data.repository.WorkoutSessionRepositoryImpl
import com.scribblefit.feature.canvas.domain.repository.CanvasRepository
import com.scribblefit.feature.canvas.domain.repository.WorkoutSessionRepository
import com.scribblefit.feature.canvas.domain.usecase.ProcessScribbleUseCase
import com.scribblefit.feature.canvas.domain.usecase.ExecuteQuickActionUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CanvasModule {

    @Binds
    @Singleton
    abstract fun bindCanvasRepository(impl: CanvasRepositoryImpl): CanvasRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutSessionRepository(impl: WorkoutSessionRepositoryImpl): WorkoutSessionRepository

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
    }
}
