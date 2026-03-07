package com.scribblefit.feature.ai.domain.model

data class AnalysisSummary(
    val period: SummaryPeriod,
    val summaryText: String,
    val highlights: List<String>,
    val focusMuscleGroups: List<String>,
    val volumeDelta: Double,
    val timestamp: Long
)

enum class SummaryPeriod {
    WEEK, MONTH, YEAR
}
