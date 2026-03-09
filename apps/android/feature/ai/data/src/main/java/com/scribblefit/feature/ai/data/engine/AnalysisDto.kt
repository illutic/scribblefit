package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.model.InsightTrend
import com.scribblefit.feature.ai.domain.model.MuscleGroupStat
import com.scribblefit.feature.ai.domain.model.SuggestionType
import kotlinx.serialization.Serializable

@Serializable
internal data class SuggestionResponseDto(val text: String, val emoji: String, val type: SuggestionType)

@Serializable
internal data class SummaryResponseDto(
    val summaryText: String,
    val highlights: List<String>,
    val muscleDistribution: List<MuscleGroupStat>,
    val focusArea: String,
    val volumeDelta: Double
)

@Serializable
internal data class InsightResponseDto(
    val estimated1RM: Double,
    val prDetected: Boolean,
    val trendDirection: InsightTrend,
    val breakdownText: String
)
