package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.ZoneOffset

class GetFrequencyInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<FrequencyData> {
        val startMillis = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        return repository.getFrequencyInsights(startMillis, endMillis)
            .flowOn(coroutineDispatcher)
    }
}
