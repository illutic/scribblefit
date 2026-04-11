package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDate
import java.time.ZoneOffset

class GetMuscleDistributionInsightsUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(start: LocalDate, end: LocalDate): Flow<List<MuscleGroupDistribution>> {
        val startMillis = start.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endMillis = end.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        return repository
            .getMuscleDistributionInsights(startMillis, endMillis)
            .flowOn(coroutineDispatcher)
    }
}
