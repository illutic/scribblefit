package com.scribblefit.feature.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AnalysisSummary(
    val period: SummaryPeriod,
    val summaryText: String,
    val highlights: List<String>,
    val muscleDistribution: List<MuscleGroupStat>,
    val focusArea: String,
    val volumeDelta: Double,
    val timestamp: Long
)

@Serializable
data class MuscleGroupStat(val muscleGroup: String, val volumePercentage: Double)

@Serializable
enum class SummaryPeriod { DAY, WEEK, MONTH, YEAR }
