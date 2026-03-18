package com.scribblefit.feature.insights.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.insights.data.InsightsRepositoryImpl
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import com.scribblefit.feature.insights.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InsightsModule {

    @Provides
    @Singleton
    fun provideInsightsRepository(
        workoutDao: WorkoutDao,
        llmEngine: LLMEngine,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): InsightsRepository {
        return InsightsRepositoryImpl(
            workoutDao = workoutDao,
            llmEngine = llmEngine,
            coroutineDispatcher = coroutineDispatcherProvider.io()
        )
    }

    @Provides
    fun provideGetVolumeInsightsUseCase(
        repository: InsightsRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetVolumeInsightsUseCase {
        return GetVolumeInsightsUseCase(repository, coroutineDispatcherProvider.default())
    }

    @Provides
    fun provideGetFrequencyInsightsUseCase(
        repository: InsightsRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetFrequencyInsightsUseCase {
        return GetFrequencyInsightsUseCase(repository, coroutineDispatcherProvider.default())
    }

    @Provides
    fun provideGetMuscleDistributionInsightsUseCase(
        repository: InsightsRepository,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): GetMuscleDistributionInsightsUseCase {
        return GetMuscleDistributionInsightsUseCase(repository, coroutineDispatcherProvider.default())
    }

    @Provides
    fun provideGetAIOverviewUseCase(
        repository: InsightsRepository
    ): GetAIOverviewUseCase {
        return GetAIOverviewUseCase(repository)
    }
}
