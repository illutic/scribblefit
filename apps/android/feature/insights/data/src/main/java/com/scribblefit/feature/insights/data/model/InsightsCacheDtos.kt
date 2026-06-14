package com.scribblefit.feature.insights.data.model

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.InsightType
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import kotlinx.serialization.Serializable

@Serializable
data class AIInsightDto(
    val insightType: String,
    val text: String
)

fun AIInsight.toDto() = AIInsightDto(insightType.name, text)
fun AIInsightDto.toDomain() = AIInsight(InsightType.valueOf(insightType), text)

@Serializable
data class VolumeDataPointDto(
    val date: Long,
    val volume: Float
)

fun VolumeDataPoint.toDto() = VolumeDataPointDto(date, volume)
fun VolumeDataPointDto.toDomain() = VolumeDataPoint(date, volume)

@Serializable
data class FrequencyDataDto(
    val workoutsPerWeek: Long,
    val totalExercises: Int
)

fun FrequencyData.toDto() = FrequencyDataDto(workoutsPerWeek, totalExercises)
fun FrequencyDataDto.toDomain() = FrequencyData(workoutsPerWeek, totalExercises)

@Serializable
data class MuscleGroupDistributionDto(
    val muscleGroup: String,
    val percentage: Float
)

fun MuscleGroupDistribution.toDto() = MuscleGroupDistributionDto(muscleGroup, percentage)
fun MuscleGroupDistributionDto.toDomain() = MuscleGroupDistribution(muscleGroup, percentage)
