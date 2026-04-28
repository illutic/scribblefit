package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.model.ExerciseTrends
import com.scribblefit.core.model.WeeklyStats

data class ExerciseDetailsState(
    val exerciseName: String = "",
    val isLoading: Boolean = false,
    val trends: ExerciseTrends? = null,
    val weeklyStats: WeeklyStats? = null,
    val aiInsight: AIInsight? = null,
    val isGeneratingAI: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
    val error: String? = null,
) {
    val weightUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = if (weightUnit == Weight.KGS) {
            stringResource(R.string.workout_exercises_weight_unit_kg)
        } else {
            stringResource(R.string.workout_exercises_weight_unit_lb)
        }

    val recommendationLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_recommendation)

    val noInsightsText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_no_insights)

    val trendsLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_trends)

    val viewAllLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_view_all)

    val current1rmLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_current_1rm)

    val intensityLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_intensity)

    val weightVsLastLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_weight_vs_last)

    val lastVolumeLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_last_volume)

    val historyLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_history)

    val viewAllSessionsText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_view_all_sessions)

    val weeklyPerformanceLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_weekly_performance)

    val activityLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_activity)

    val sessionsUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_sessions_unit)

    val volumeLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_volume)

    val maxWeightLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_details_max_weight)

    @Composable
    @ReadOnlyComposable
    fun getTotalSessionsText(count: Int): String =
        stringResource(R.string.exercise_details_total_sessions_format, count)

    @Composable
    @ReadOnlyComposable
    fun getTrendDirectionText(direction: com.scribblefit.core.model.TrendDirection): String =
        when (direction) {
            com.scribblefit.core.model.TrendDirection.IMPROVING -> stringResource(R.string.trend_improving)
            com.scribblefit.core.model.TrendDirection.STABLE -> stringResource(R.string.trend_stable)
            com.scribblefit.core.model.TrendDirection.PLATEAUED -> stringResource(R.string.trend_plateaued)
            com.scribblefit.core.model.TrendDirection.DECLINING -> stringResource(R.string.trend_declining)
        }
}
