package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class GetAIOverviewUseCase(
    private val repository: InsightsRepository,
    private val scribbleRepository: ScribbleRepository,
) {
    suspend operator fun invoke(
        currentDate: LocalDate = LocalDate.now(),
        lookUpDays: Long = 7
    ) = invoke(
        startDate = currentDate.minusDays(lookUpDays),
        endDate = currentDate
    )

    suspend operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Result<List<AIInsight>> {
        val startMillis = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endMillis = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val scribbles = scribbleRepository.getScribblesInRange(startMillis, endMillis).first()
        val exercises = scribbles.flatMap { it.exercises }
        return repository.getAIOverview(exercises)
    }
}
