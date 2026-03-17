package com.scribblefit.feature.insights.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint

data class InsightsState(
    val isLoading: Boolean = true,
    val volumePoints: List<VolumeDataPoint> = emptyList(),
    val frequency: FrequencyData? = null,
    val distribution: List<MuscleGroupDistribution> = emptyList(),
    val errorMessage: String? = null,
    val bottomBarState: BottomBarState = BottomBarState(selectedTab = Screen.Insights)
) {
    val isEmpty: Boolean
        get() = !isLoading && (frequency == null || frequency.totalWorkouts < 2)

    val titleRes: Int = R.string.insights_title
    val emptyTitleRes: Int = R.string.insights_empty_title
    val emptyDescriptionRes: Int = R.string.insights_empty_description
    val loadingRes: Int = R.string.insights_loading
    val totalWorkoutsRes: Int = R.string.insights_total_workouts
    val workoutsPerWeekRes: Int = R.string.insights_workouts_per_week
    val volumeChartTitleRes: Int = R.string.insights_volume_chart_title
    val muscleDistributionTitleRes: Int = R.string.insights_muscle_distribution_title
}

@Composable
@ReadOnlyComposable
fun InsightsState.getTitle(): String = stringResource(titleRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getEmptyTitle(): String = stringResource(emptyTitleRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getEmptyDescription(): String = stringResource(emptyDescriptionRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getLoadingText(): String = stringResource(loadingRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getTotalWorkoutsLabel(): String = stringResource(totalWorkoutsRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getWorkoutsPerWeekLabel(): String = stringResource(workoutsPerWeekRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getVolumeChartTitle(): String = stringResource(volumeChartTitleRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getMuscleDistributionTitle(): String = stringResource(muscleDistributionTitleRes)
