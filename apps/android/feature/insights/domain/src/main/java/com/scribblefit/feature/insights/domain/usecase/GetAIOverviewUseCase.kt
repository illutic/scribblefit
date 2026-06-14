package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import java.time.LocalDateTime

class GetAIOverviewUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        currentDate: CurrentDate = CurrentDate(LocalDateTime.now()),
        lookUpDays: Long = 7
    ) = invoke(
        startDate = CurrentDate(currentDate.date.minusDays(lookUpDays)),
        endDate = currentDate
    )

    operator fun invoke(
        startDate: CurrentDate,
        endDate: CurrentDate
    ): Flow<List<AIInsight>> =
        repository.getAIOverview(
            startDate = startDate.millis,
            endDate = endDate.millis
        ).flowOn(coroutineDispatcher)
}
