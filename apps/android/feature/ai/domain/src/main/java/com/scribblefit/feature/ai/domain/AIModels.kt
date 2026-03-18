package com.scribblefit.feature.ai.domain

data class SummaryInput(
    val volumeTrend: String,
    val frequencyStats: String,
    val muscleDistribution: String
)

data class SummaryResult(
    val summary: String,
    val trends: String,
    val advice: String
)
