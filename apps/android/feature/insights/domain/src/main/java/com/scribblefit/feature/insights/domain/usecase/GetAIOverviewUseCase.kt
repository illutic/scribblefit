package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.core.model.AIInsight
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneOffset

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
        val startDate = startDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val endDate = endDate.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

        val scribbles = scribbleRepository.getScribblesInRange(startDate, endDate).first()
        val exercises = scribbles.flatMap { it.exercises }
        return repository.getAIOverview(exercises)
    }
}
