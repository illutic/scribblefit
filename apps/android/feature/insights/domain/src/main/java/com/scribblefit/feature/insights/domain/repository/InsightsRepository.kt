package com.scribblefit.feature.insights.domain.repository

import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface InsightsRepository {
    fun getVolumeInsights(startDate: Long, endDate: Long): Flow<List<VolumeDataPoint>>
    fun getFrequencyInsights(startDate: Long, endDate: Long): Flow<FrequencyData>
    fun getMuscleDistributionInsights(startDate: Long, endDate: Long): Flow<List<MuscleGroupDistribution>>
    suspend fun getAIOverview(exercises: List<Exercise>): Result<List<AIInsight>>
}
