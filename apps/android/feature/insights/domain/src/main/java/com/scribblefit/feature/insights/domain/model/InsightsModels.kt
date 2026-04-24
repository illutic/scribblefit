package com.scribblefit.feature.insights.domain.model

import com.scribblefit.core.model.AIInsight

data class VolumeDataPoint(
    val date: Long,
    val volume: Float
)

data class FrequencyData(
    val workoutsPerWeek: Long,
    val totalExercises: Int = 0
)

data class MuscleGroupDistribution(
    val muscleGroup: String,
    val percentage: Float
)

data class Insights(
    val volumePoints: List<VolumeDataPoint>,
    val frequency: FrequencyData,
    val distribution: List<MuscleGroupDistribution>,
    val aiOverview: List<AIInsight>? = null
)

