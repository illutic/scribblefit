package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.TrendDirection
import com.scribblefit.feature.exercises.domain.usecase.TrendDataPoint
import com.scribblefit.feature.exercises.domain.usecase.TrendInsights
import com.scribblefit.feature.exercises.domain.usecase.TrendPeriod

data class ExerciseTrendsState(
    val exerciseName: String = "",
    val isLoading: Boolean = false,
    
    // OneRM Data
    val oneRMDataPoints: List<TrendDataPoint> = emptyList(),
    val oneRMInsights: TrendInsights? = null,
    
    // Volume Data
    val volumeDataPoints: List<TrendDataPoint> = emptyList(),
    val volumeInsights: TrendInsights? = null,
    
    val weightUnit: Weight = Weight.LBS,
    val selectedPeriod: TrendPeriod = TrendPeriod.THREE_MONTHS,
    val error: String? = null
) {
    val navigationTitle: String get() = exerciseName
    
    val oneRMSectionTitle: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_trends_estimated_1rm)
        
    val volumeSectionTitle: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_trends_total_volume)
        
    val emptyDataMessage: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_trends_no_data)
        
    val periodPickerLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_trends_period_label)
        
    val personalBestLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.exercise_trends_pb_label)
        
    val weightUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = if (weightUnit == Weight.KGS) {
            stringResource(R.string.workout_exercises_weight_unit_kg)
        } else {
            stringResource(R.string.workout_exercises_weight_unit_lb)
        }
        
    @Composable
    @ReadOnlyComposable
    fun getPeriodText(period: TrendPeriod): String =
        when (period) {
            TrendPeriod.ONE_MONTH -> stringResource(R.string.exercise_trends_period_1m)
            TrendPeriod.THREE_MONTHS -> stringResource(R.string.exercise_trends_period_3m)
            TrendPeriod.SIX_MONTHS -> stringResource(R.string.exercise_trends_period_6m)
            TrendPeriod.ONE_YEAR -> stringResource(R.string.exercise_trends_period_1y)
            TrendPeriod.ALL -> stringResource(R.string.exercise_trends_period_all)
        }

    @Composable
    @ReadOnlyComposable
    fun getTrendBadgeText(direction: TrendDirection, percentage: Float): String =
        when (direction) {
            TrendDirection.IMPROVING -> "+${percentage.toInt()}%"
            TrendDirection.STABLE -> stringResource(R.string.trend_stable)
            TrendDirection.PLATEAUED -> stringResource(R.string.trend_plateaued)
            TrendDirection.DECLINING -> "${percentage.toInt()}%"
        }
}
