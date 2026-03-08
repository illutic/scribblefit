package com.scribblefit.feature.ai.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseInsight(
    val exerciseId: String,
    val estimated1RM: Double,
    val prDetected: Boolean,
    val trendDirection: InsightTrend,
    val breakdownText: String,
    val timestamp: Long
)

@Serializable
enum class InsightTrend { IMPROVING, STABLE, PLATEAUED, DECLINING }
