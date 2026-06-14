package com.scribblefit.feature.insights.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.feature.ai.domain.LLMEngine
import com.scribblefit.feature.insights.data.InsightsRepositoryImpl
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.insights.domain.usecase.GetFrequencyInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetMuscleDistributionInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetVolumeInsightsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object InsightsModule {

    @Provides
    @Singleton
    fun provideInsightsRepository(
        llmEngine: LLMEngine,
        database: ScribbleFitDatabase,
        dispatcherProvider: CoroutineDispatcherProvider
    ): InsightsRepository {
        return InsightsRepositoryImpl(
            exerciseDao = database.exerciseDao(),
            llmEngine = llmEngine,
            coroutineDispatcher = dispatcherProvider.io()
        )
    }

    @Provides
    @Singleton
    fun provideGetVolumeInsightsUseCase(
        repository: InsightsRepository,
        dispatcherProvider: CoroutineDispatcherProvider
    ): GetVolumeInsightsUseCase = GetVolumeInsightsUseCase(repository, dispatcherProvider.io())

    @Provides
    @Singleton
    fun provideGetFrequencyInsightsUseCase(
        repository: InsightsRepository,
        dispatcherProvider: CoroutineDispatcherProvider
    ): GetFrequencyInsightsUseCase =
        GetFrequencyInsightsUseCase(repository, dispatcherProvider.io())

    @Provides
    @Singleton
    fun provideGetMuscleDistributionInsightsUseCase(
        repository: InsightsRepository,
        dispatcherProvider: CoroutineDispatcherProvider
    ): GetMuscleDistributionInsightsUseCase =
        GetMuscleDistributionInsightsUseCase(repository, dispatcherProvider.io())

    @Provides
    @Singleton
    fun provideGetAIOverviewUseCase(
        insightsRepository: InsightsRepository,
        coroutineProvider: CoroutineDispatcherProvider
    ): GetAIOverviewUseCase = GetAIOverviewUseCase(
        repository = insightsRepository,
        coroutineDispatcher = coroutineProvider.io()
    )
}
