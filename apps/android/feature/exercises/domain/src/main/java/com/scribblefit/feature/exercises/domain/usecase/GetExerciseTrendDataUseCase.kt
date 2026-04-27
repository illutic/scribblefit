package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.Calculations
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.Calendar

/**
 * Metric types for trend visualization.
 */
enum class TrendMetric {
    ONE_RM,
    VOLUME,
    MAX_WEIGHT
}

/**
 * Time periods for filtering trend data.
 */
enum class TrendPeriod {
    ONE_MONTH,
    THREE_MONTHS,
    SIX_MONTHS,
    ONE_YEAR,
    ALL
}

/**
 * Data point for a trend chart.
 */
data class TrendDataPoint(
    val date: Long,
    val value: Float
)

/**
 * Insights calculated from trend data.
 */
data class TrendInsights(
    val personalBest: Float,
    val percentageChange: Float,
    val trendDirection: com.scribblefit.core.model.TrendDirection
)

/**
 * Result model containing all data for the trends screen.
 */
data class ExerciseTrendResult(
    val dataPoints: List<TrendDataPoint>,
    val insights: TrendInsights
)

/**
 * Use case to fetch and calculate trend data for an exercise.
 */
class GetExerciseTrendDataUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(
        exerciseName: String,
        metric: TrendMetric,
        period: TrendPeriod
    ): Result<ExerciseTrendResult> = withContext(coroutineDispatcher) {
        runCatching {
            val allHistory = exerciseRepository.getExercisesByName(exerciseName)
                .sortedBy { it.createdAt }

            if (allHistory.isEmpty()) {
                return@runCatching ExerciseTrendResult(
                    emptyList(),
                    TrendInsights(0f, 0f, com.scribblefit.core.model.TrendDirection.STABLE)
                )
            }

            val filteredHistory = filterByPeriod(allHistory, period)
            
            val dataPoints = filteredHistory.map { exercise ->
                val value = when (metric) {
                    TrendMetric.ONE_RM -> {
                        exercise.sets.maxOfOrNull { 
                            Calculations.calculate1RM(it.weight ?: 0f, it.reps) 
                        } ?: 0f
                    }
                    TrendMetric.VOLUME -> {
                        exercise.sets.sumOf { 
                            Calculations.calculateVolume(it.weight, it.reps).toDouble() 
                        }.toFloat()
                    }
                    TrendMetric.MAX_WEIGHT -> {
                        exercise.sets.maxOfOrNull { it.weight ?: 0f } ?: 0f
                    }
                }
                TrendDataPoint(exercise.createdAt, value)
            }

            val personalBest = dataPoints.maxOfOrNull { it.value } ?: 0f
            
            // Calculate percentage change between start and end of period
            val percentageChange = if (dataPoints.size >= 2) {
                val first = dataPoints.first().value
                val last = dataPoints.last().value
                if (first > 0) ((last - first) / first) * 100f else 0f
            } else 0f

            val direction = when {
                percentageChange > 5f -> com.scribblefit.core.model.TrendDirection.IMPROVING
                percentageChange < -5f -> com.scribblefit.core.model.TrendDirection.DECLINING
                else -> com.scribblefit.core.model.TrendDirection.STABLE
            }

            ExerciseTrendResult(
                dataPoints = dataPoints,
                insights = TrendInsights(personalBest, percentageChange, direction)
            )
        }
    }

    private fun filterByPeriod(
        history: List<com.scribblefit.core.model.Exercise>, 
        period: TrendPeriod
    ): List<com.scribblefit.core.model.Exercise> {
        if (period == TrendPeriod.ALL) return history

        val calendar = Calendar.getInstance()
        when (period) {
            TrendPeriod.ONE_MONTH -> calendar.add(Calendar.MONTH, -1)
            TrendPeriod.THREE_MONTHS -> calendar.add(Calendar.MONTH, -3)
            TrendPeriod.SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
            TrendPeriod.ONE_YEAR -> calendar.add(Calendar.YEAR, -1)
            else -> {}
        }
        val startTime = calendar.timeInMillis
        return history.filter { it.createdAt >= startTime }
    }
}
