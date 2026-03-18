package com.scribblefit.feature.insights.domain.repository

import com.scribblefit.feature.insights.domain.model.AIOverview
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface InsightsRepository {
    fun getVolumeInsights(startDate: LocalDate, endDate: LocalDate): Flow<List<VolumeDataPoint>>
    fun getFrequencyInsights(): Flow<FrequencyData>
    fun getMuscleDistributionInsights(): Flow<List<MuscleGroupDistribution>>
    suspend fun getAIOverview(): Result<AIOverview>
}
