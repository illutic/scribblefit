package com.scribblefit.feature.ai.data.mapper

import com.scribblefit.feature.ai.domain.model.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuggestionDto(
    val text: String,
    val emoji: String,
    val type: String,
    val timestamp: Long? = null
)

@Serializable
data class SummaryDto(
    @SerialName("summary_text") val summaryText: String,
    val highlights: List<String>,
    @SerialName("focus_muscle_groups") val focusMuscleGroups: List<String>,
    @SerialName("volume_delta") val volumeDelta: Double,
    val timestamp: Long? = null
)

@Serializable
data class ExerciseInsightDto(
    @SerialName("estimated_1rm") val estimated1rm: Double,
    @SerialName("pr_detected") val prDetected: Boolean,
    @SerialName("trend_direction") val trendDirection: String,
    @SerialName("breakdown_text") val breakdownText: String,
    val timestamp: Long? = null
)

fun SuggestionDto.toDomain() = AnalysisSuggestion(
    text = text,
    emoji = emoji,
    type = SuggestionType.valueOf(type.uppercase()),
    timestamp = timestamp ?: System.currentTimeMillis()
)

fun AnalysisSuggestion.toDto() = SuggestionDto(
    text = text,
    emoji = emoji,
    type = type.name,
    timestamp = timestamp
)

fun SummaryDto.toDomain(period: SummaryPeriod) = AnalysisSummary(
    period = period,
    summaryText = summaryText,
    highlights = highlights,
    focusMuscleGroups = focusMuscleGroups,
    volumeDelta = volumeDelta,
    timestamp = timestamp ?: System.currentTimeMillis()
)

fun AnalysisSummary.toDto() = SummaryDto(
    summaryText = summaryText,
    highlights = highlights,
    focusMuscleGroups = focusMuscleGroups,
    volumeDelta = volumeDelta,
    timestamp = timestamp
)

fun ExerciseInsightDto.toDomain(exerciseId: String) = ExerciseInsight(
    exerciseId = exerciseId,
    estimated1RM = estimated1rm,
    prDetected = prDetected,
    trendDirection = InsightTrend.valueOf(trendDirection.uppercase()),
    breakdownText = breakdownText,
    timestamp = timestamp ?: System.currentTimeMillis()
)

fun ExerciseInsight.toDto() = ExerciseInsightDto(
    estimated1rm = estimated1RM,
    prDetected = prDetected,
    trendDirection = trendDirection.name,
    breakdownText = breakdownText,
    timestamp = timestamp
)
