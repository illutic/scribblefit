package com.scribblefit.feature.insights.data.di

import com.scribblefit.core.coroutines.CoroutineDispatcherProvider
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.feature.insights.data.InsightsRepositoryImpl
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
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
object InsightsModule {

    @Provides
    @Singleton
    fun provideInsightsRepository(
        workoutDao: WorkoutDao,
        coroutineDispatcherProvider: CoroutineDispatcherProvider
    ): InsightsRepository {
        return InsightsRepositoryImpl(
            workoutDao = workoutDao,
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
}
