package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class GetMuscleDistributionInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        start: CurrentDate,
        end: CurrentDate
    ): Result<List<MuscleGroupDistribution>> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                repository.getMuscleDistributionInsights(
                    startDate = start.startOfDayInMillis,
                    endDate = end.startOfDayInMillis
                ).flowOn(coroutineDispatcher).firstOrNull() ?: emptyList()
            }
        }
}
