package com.scribblefit.feature.insights.domain.model

import java.time.LocalDate

data class VolumeDataPoint(
    val date: LocalDate,
    val volume: Float
)

data class FrequencyData(
    val totalWorkouts: Int,
    val workoutsPerWeek: Float
)

data class MuscleGroupDistribution(
    val muscleGroup: String,
    val percentage: Float
)

data class AIOverview(
    val summary: String,
    val trends: String,
    val advice: String
)

data class Insights(
    val volumePoints: List<VolumeDataPoint>,
    val frequency: FrequencyData,
    val distribution: List<MuscleGroupDistribution>,
    val aiOverview: AIOverview? = null
)

