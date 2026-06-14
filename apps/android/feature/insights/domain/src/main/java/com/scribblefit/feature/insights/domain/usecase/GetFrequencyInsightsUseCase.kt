package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetFrequencyInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        startDate: CurrentDate,
        endDate: CurrentDate
    ): Flow<FrequencyData> = repository.getFrequencyInsights(
        startDate = startDate.millis,
        endDate = endDate.millis
    ).flowOn(coroutineDispatcher)
}
