package com.scribblefit.feature.insights.domain.repository

import com.scribblefit.core.model.AIInsight
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import kotlinx.coroutines.flow.Flow

interface InsightsRepository {
    fun getVolumeInsights(startDate: Long, endDate: Long): Flow<List<VolumeDataPoint>>
    fun getFrequencyInsights(startDate: Long, endDate: Long): Flow<FrequencyData>
    fun getMuscleDistributionInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<MuscleGroupDistribution>>

    fun getAIOverview(startDate: Long, endDate: Long): Flow<List<AIInsight>>
}
