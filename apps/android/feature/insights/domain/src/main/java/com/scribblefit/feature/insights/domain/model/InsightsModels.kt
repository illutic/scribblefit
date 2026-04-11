package com.scribblefit.feature.insights.domain.model

import com.scribblefit.core.model.AIInsight
import java.time.LocalDate

data class VolumeDataPoint(
    val date: LocalDate,
    val volume: Float
)

data class FrequencyData(
    val totalWorkouts: Int,
    val workoutsPerWeek: Float,
    val totalExercises: Int = 0
)

data class MuscleGroupDistribution(
    val muscleGroup: String,
    val percentage: Float
)

data class AIOverview(
    val insights: List<AIInsight>
)

data class Insights(
    val volumePoints: List<VolumeDataPoint>,
    val frequency: FrequencyData,
    val distribution: List<MuscleGroupDistribution>,
    val aiOverview: AIOverview? = null
)

