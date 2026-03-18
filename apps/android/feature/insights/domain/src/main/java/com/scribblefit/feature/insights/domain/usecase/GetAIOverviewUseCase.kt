package com.scribblefit.feature.insights.domain.usecase

import com.scribblefit.feature.insights.domain.model.AIOverview
import com.scribblefit.feature.insights.domain.repository.InsightsRepository

class GetAIOverviewUseCase(
    private val repository: InsightsRepository
) {
    suspend operator fun invoke(): Result<AIOverview> = repository.getAIOverview()
}
