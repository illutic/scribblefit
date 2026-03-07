package com.scribblefit.feature.ai.domain.model

data class AnalysisSummary(
    val period: SummaryPeriod,
    val summaryText: String,
    val highlights: List<String>,
    val muscleDistribution: List<MuscleGroupStat>,
    val focusArea: String,
    val volumeDelta: Double,
    val timestamp: Long
)

data class MuscleGroupStat(
    val muscleGroup: String,
    val volumePercentage: Double
)

enum class SummaryPeriod {
    DAY, WEEK, MONTH, YEAR
}
