package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Exercise(
    val id: Long,
    val canonicalName: String,
    val muscleGroup: String,
    val sets: List<Set>,
    val createdAt: Long,
    val isDraft: Boolean = false
)

@Serializable
data class WeeklyStats(
    val sessions: Int,
    val totalVolume: Float,
    val maxWeight: Float
)

@Serializable
data class ExerciseTrends(
    val estimated1RM: Float,
    val intensity: Float,
    val improvement: Float,
    val trendDirection: TrendDirection,
    val lastVolume: Float = 0f,
    val lastVolumeTrend: TrendDirection = TrendDirection.STABLE
)