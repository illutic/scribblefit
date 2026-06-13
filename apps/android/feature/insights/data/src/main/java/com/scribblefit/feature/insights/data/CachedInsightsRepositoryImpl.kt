package com.scribblefit.feature.insights.data

import com.scribblefit.core.model.AIInsight
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import com.scribblefit.feature.insights.domain.repository.InsightsRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

private typealias DateRange = Pair<Long, Long>

class CachedInsightsRepositoryImpl @Inject constructor(
    private val insightsRepository: InsightsRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) : InsightsRepository {
    private val cachedOverview = mutableMapOf<String, List<AIInsight>>()
    private val cachedVolumeInsights = mutableMapOf<DateRange, List<VolumeDataPoint>>()
    private val cachedFrequencyInsights = mutableMapOf<DateRange, FrequencyData>()
    private val cachedMuscleDistributionInsights =
        mutableMapOf<DateRange, List<MuscleGroupDistribution>>()

    override fun getVolumeInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<VolumeDataPoint>> {
        val dateRange = startDate to endDate
        val cachedData = cachedVolumeInsights[dateRange]
        return if (cachedData != null) {
            flowOf(cachedData)
        } else {
            insightsRepository
                .getVolumeInsights(startDate, endDate)
                .flowOn(coroutineDispatcher)
                .map { data ->
                    cachedVolumeInsights[dateRange] = data
                    data
                }
        }
    }

    override fun getFrequencyInsights(
        startDate: Long,
        endDate: Long
    ): Flow<FrequencyData> {
        val dateRange = startDate to endDate
        val cachedData = cachedFrequencyInsights[dateRange]
        return if (cachedData != null) {
            flowOf(cachedData)
        } else {
            insightsRepository
                .getFrequencyInsights(startDate, endDate)
                .flowOn(coroutineDispatcher)
                .map { data ->
                    cachedFrequencyInsights[dateRange] = data
                    data
                }
        }
    }

    override fun getMuscleDistributionInsights(
        startDate: Long,
        endDate: Long
    ): Flow<List<MuscleGroupDistribution>> {
        val dateRange = startDate to endDate
        val cachedData = cachedMuscleDistributionInsights[dateRange]
        return if (cachedData != null) {
            flowOf(cachedData)
        } else {
            insightsRepository
                .getMuscleDistributionInsights(startDate, endDate)
                .flowOn(coroutineDispatcher)
                .map { data ->
                    cachedMuscleDistributionInsights[dateRange] = data
                    data
                }
        }
    }

    override fun getAIOverview(
        startDate: Long,
        endDate: Long
    ): Flow<List<AIInsight>> {
        val cacheKey = "$startDate-$endDate"
        val cachedData = cachedOverview[cacheKey]
        return if (cachedData != null) {
            flowOf(cachedData)
        } else {
            insightsRepository
                .getAIOverview(startDate, endDate)
                .flowOn(coroutineDispatcher)
                .map { data ->
                    cachedOverview[cacheKey] = data
                    data
                }
        }
    }
}
