package com.scribblefit.feature.ai.data.engine

import com.scribblefit.feature.ai.domain.model.AnalysisSuggestion
import com.scribblefit.feature.ai.domain.model.AnalysisSummary
import com.scribblefit.feature.ai.domain.model.ExerciseInsight
import com.scribblefit.feature.ai.domain.model.InsightTrend
import com.scribblefit.feature.ai.domain.model.MuscleGroupStat
import com.scribblefit.feature.ai.domain.model.SuggestionType
import com.scribblefit.feature.ai.domain.model.SummaryPeriod
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

@Serializable
@InternalSerializationApi
internal data class SuggestionResponseDto(
    val text: String,
    val type: SuggestionType
)

@Serializable
@InternalSerializationApi
internal data class SummaryResponseDto(
    val summaryText: String,
    val highlights: List<String>,
    val muscleDistribution: List<MuscleGroupStatDto>,
    val focusArea: String,
    val volumeDelta: Double
)

@Serializable
@InternalSerializationApi
internal data class InsightResponseDto(
    val estimated1RM: Double,
    val prDetected: Boolean,
    val trendDirection: InsightTrend,
    val breakdownText: String
)

@Serializable
@InternalSerializationApi
internal data class MuscleGroupStatDto(val muscleGroup: String, val volumePercentage: Double)

@InternalSerializationApi
internal fun SummaryResponseDto.toDomain(period: SummaryPeriod): AnalysisSummary = AnalysisSummary(
    period = period,
    summaryText = summaryText,
    highlights = highlights,
    muscleDistribution = muscleDistribution.map { it.toDomain() },
    focusArea = focusArea,
    volumeDelta = volumeDelta,
    timestamp = System.currentTimeMillis()
)

@InternalSerializationApi
internal fun MuscleGroupStatDto.toDomain(): MuscleGroupStat = MuscleGroupStat(
    muscleGroup = muscleGroup,
    volumePercentage = volumePercentage
)

@InternalSerializationApi
internal fun SuggestionResponseDto.toDomain(): AnalysisSuggestion = AnalysisSuggestion(
    text = text,
    type = type,
    timestamp = System.currentTimeMillis(),
    displayText = text
)

@InternalSerializationApi
internal fun InsightResponseDto.toDomain(exerciseId: String): ExerciseInsight = ExerciseInsight(
    exerciseId = exerciseId,
    estimated1RM = estimated1RM,
    prDetected = prDetected,
    trendDirection = trendDirection,
    breakdownText = breakdownText,
    timestamp = System.currentTimeMillis()
)