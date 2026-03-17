package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetMuscleDistributionInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(): Flow<List<MuscleGroupDistribution>> {
        return repository.getMuscleDistributionInsights()
            .flowOn(coroutineDispatcher)
    }
}
