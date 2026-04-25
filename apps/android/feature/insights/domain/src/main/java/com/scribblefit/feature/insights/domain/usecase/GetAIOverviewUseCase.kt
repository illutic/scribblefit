package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.time.LocalDate

class GetAIOverviewUseCase(
    private val repository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        currentDate: CurrentDate = CurrentDate(LocalDate.now()),
        lookUpDays: Long = 7
    ) = invoke(
        startDate = CurrentDate(currentDate.date.minusDays(lookUpDays)),
        endDate = currentDate
    )

    suspend operator fun invoke(
        startDate: CurrentDate,
        endDate: CurrentDate
    ): Result<List<AIInsight>> = withContext(coroutineDispatcher) {
        runCatchingWithCancellation {
            repository.getAIOverview(
                startDate = startDate.startOfDayInMillis,
                endDate = endDate.startOfDayInMillis
            )
        }
    }
}
