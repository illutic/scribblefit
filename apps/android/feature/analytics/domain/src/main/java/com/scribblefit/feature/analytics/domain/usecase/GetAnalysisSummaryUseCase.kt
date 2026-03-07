package com.scribblefit.feature.analytics.domain.usecase

import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import com.scribblefit.feature.analytics.domain.repository.AnalysisRepository
import kotlinx.coroutines.flow.Flow

class GetAnalysisSummaryUseCase(
    private val repository: AnalysisRepository
) {
    operator fun invoke(period: SummaryPeriod): Flow<AnalysisSummary?> {
        return repository.getSummary(period)
    }
}
