package com.scribblefit.feature.analytics.data.di

import com.scribblefit.feature.analytics.data.repository.AnalysisRepositoryImpl
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import com.scribblefit.feature.analytics.domain.usecase.GetAnalysisSummaryUseCase
import com.scribblefit.feature.analytics.domain.usecase.AnalyzeWorkoutsUseCase
import com.scribblefit.core.ai.engine.AnalysisEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalysisRepository(impl: AnalysisRepositoryImpl): AnalysisRepository

    companion object {
        @Provides
        @Singleton
        fun provideGetAnalysisSummaryUseCase(repository: AnalysisRepository): GetAnalysisSummaryUseCase {
            return GetAnalysisSummaryUseCase(repository)
        }

        @Provides
        @Singleton
        fun provideAnalyzeWorkoutsUseCase(
            repository: AnalysisRepository,
            engine: AnalysisEngine
        ): AnalyzeWorkoutsUseCase {
            return AnalyzeWorkoutsUseCase(repository, engine)
        }
    }
}
