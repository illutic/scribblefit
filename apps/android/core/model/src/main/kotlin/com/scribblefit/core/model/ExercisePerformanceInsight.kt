package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ExercisePerformanceInsight(
    val estimated1RM: Float,
    val prDetected: Boolean,
    val trendDirection: TrendDirection,
    val breakdownText: String
)
