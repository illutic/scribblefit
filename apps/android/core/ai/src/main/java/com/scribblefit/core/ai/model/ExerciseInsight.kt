package com.scribblefit.core.ai.model

data class ExerciseInsight(
    val exerciseId: String,
    val estimated1RM: Double,
    val prDetected: Boolean,
    val trendDirection: InsightTrend,
    val breakdownText: String,
    val timestamp: Long
)

enum class InsightTrend {
    IMPROVING, STABLE, PLATEAUED, DECLINING
}
