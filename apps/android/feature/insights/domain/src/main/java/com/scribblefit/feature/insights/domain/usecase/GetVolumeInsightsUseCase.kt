package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class GetVolumeInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        startDate: CurrentDate,
        endDate: CurrentDate
    ): Result<List<VolumeDataPoint>> =
        withContext(coroutineDispatcher) {
            runCatchingWithCancellation {
                repository.getVolumeInsights(
                    startDate = startDate.startOfDayInMillis,
                    endDate = endDate.startOfDayInMillis
                ).flowOn(coroutineDispatcher).firstOrNull() ?: emptyList()
            }
        }
}
