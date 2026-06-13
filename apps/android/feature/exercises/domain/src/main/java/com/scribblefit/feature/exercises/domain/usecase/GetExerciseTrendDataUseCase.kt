package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.common.Calculations
import com.scribblefit.core.common.runCatchingWithCancellation
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.feature.exercises.domain.ExerciseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.util.Calendar

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
    val trendDirection: TrendDirection
)

/**
 * Trend data for a specific metric.
 */
data class MetricTrendData(
    val dataPoints: List<TrendDataPoint>,
    val insights: TrendInsights
)

/**
 * Result model containing all data for the trends screen.
 */
data class ExerciseTrendResult(
    val oneRM: MetricTrendData,
    val volume: MetricTrendData
)

/**
 * Use case to fetch and calculate trend data for an exercise.
 */
class GetExerciseTrendDataUseCase(
    private val exerciseRepository: ExerciseRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        exerciseName: String,
        period: TrendPeriod
    ): Flow<Result<ExerciseTrendResult>> = exerciseRepository.getExercisesByNameFlow(exerciseName)
        .map { allHistory ->
            runCatchingWithCancellation {
                val sortedHistory = allHistory.sortedBy { it.createdAt }

                if (sortedHistory.isEmpty()) {
                    val emptyData = MetricTrendData(
                        emptyList(),
                        TrendInsights(0f, 0f, TrendDirection.STABLE)
                    )
                    return@runCatchingWithCancellation ExerciseTrendResult(
                        oneRM = emptyData,
                        volume = emptyData
                    )
                }

                val filteredHistory = filterByPeriod(sortedHistory, period)

                val oneRMData = calculateMetricData(filteredHistory) { exercise ->
                    exercise.sets.maxOfOrNull {
                        Calculations.calculate1RM(it.weight ?: 0f, it.reps)
                    } ?: 0f
                }

                val volumeData = calculateMetricData(filteredHistory) { exercise ->
                    exercise.sets.sumOf {
                        Calculations.calculateVolume(it.weight, it.reps).toDouble()
                    }.toFloat()
                }

                ExerciseTrendResult(
                    oneRM = oneRMData,
                    volume = volumeData
                )
            }
        }
        .flowOn(coroutineDispatcher)

    private fun calculateMetricData(
        history: List<Exercise>,
        valueExtractor: (Exercise) -> Float
    ): MetricTrendData {
        val dataPoints = history.map { exercise ->
            TrendDataPoint(exercise.createdAt, valueExtractor(exercise))
        }

        val personalBest = dataPoints.maxOfOrNull { it.value } ?: 0f

        // Calculate percentage change between start and end of period
        val percentageChange = if (dataPoints.size >= 2) {
            val first = dataPoints.first().value
            val last = dataPoints.last().value
            if (first > 0) ((last - first) / first) * 100f else 0f
        } else 0f

        val direction = when {
            percentageChange > 5f -> TrendDirection.IMPROVING
            percentageChange < -5f -> TrendDirection.DECLINING
            else -> TrendDirection.STABLE
        }

        return MetricTrendData(
            dataPoints = dataPoints,
            insights = TrendInsights(personalBest, percentageChange, direction)
        )
    }

    private fun filterByPeriod(
        history: List<Exercise>,
        period: TrendPeriod
    ): List<Exercise> {
        if (period == TrendPeriod.ALL) return history

        val calendar = Calendar.getInstance()
        when (period) {
            TrendPeriod.ONE_MONTH -> calendar.add(Calendar.MONTH, -1)
            TrendPeriod.THREE_MONTHS -> calendar.add(Calendar.MONTH, -3)
            TrendPeriod.SIX_MONTHS -> calendar.add(Calendar.MONTH, -6)
            TrendPeriod.ONE_YEAR -> calendar.add(Calendar.YEAR, -1)
        }
        val startTime = calendar.timeInMillis
        return history.filter { it.createdAt >= startTime }
    }
}
