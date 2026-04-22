package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDetails(
    val exerciseName: String,
    val muscleGroup: String,
    val weeklyStats: WeeklyStats,
    val trends: ExerciseTrends,
    val history: List<ExerciseHistorySession>
)

@Serializable
data class WeeklyStats(
    val sessionsThisWeek: Int,
    val totalVolumeThisWeek: Float,
    val maxWeightThisWeek: Float
)

@Serializable
data class ExerciseTrends(
    val current1RM: Float,
    val trendDirection: TrendDirection,
    val lastVolume: Float,
    val lastVolumeTrend: TrendDirection
)
