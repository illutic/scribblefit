package com.scribblefit.feature.insights.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.model.AIInsight
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.insights.domain.model.FrequencyData
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution
import com.scribblefit.feature.insights.domain.model.VolumeDataPoint
import java.time.LocalDate

enum class InsightsPeriod {
    DAILY, WEEKLY, MONTHLY
}

data class InsightsState(
    val isLoading: Boolean = true,
    val isGeneratingAI: Boolean = false,
    val volumePoints: List<VolumeDataPoint> = emptyList(),
    val frequency: FrequencyData? = null,
    val distribution: List<MuscleGroupDistribution> = emptyList(),
    val insights: List<AIInsight>? = null,
    val errorMessage: String? = null,
    val selectedPeriod: InsightsPeriod = InsightsPeriod.WEEKLY,
    val startDate: LocalDate = LocalDate.now().minusWeeks(1),
    val endDate: LocalDate = LocalDate.now(),
    val bottomBarState: BottomBarState = BottomBarState(selectedTab = Screen.Insights),
) {
    val isEmpty: Boolean
        get() = !isLoading && (frequency == null || frequency.totalExercises < 2)

    val titleRes: Int = R.string.insights_title
    val emptyTitleRes: Int = R.string.insights_empty_title
    val emptyDescriptionRes: Int = R.string.insights_empty_description
    val loadingRes: Int = R.string.insights_loading
    val totalWorkoutsRes: Int = R.string.insights_total_workouts
    val workoutsPerWeekRes: Int = R.string.insights_workouts_per_week
    val volumeChartTitleRes: Int = R.string.insights_volume_chart_title
    val muscleDistributionTitleRes: Int = R.string.insights_muscle_distribution_title
    val aiOverviewTitleRes: Int = R.string.insights_ai_overview_title
    val aiOverviewGeneratingRes: Int = R.string.insights_ai_overview_generating
    val loadingSubtitleRes: Int = R.string.insights_loading_subtitle
    val sessionsRes: Int = R.string.insights_sessions
    val totalVolumeRes: Int = R.string.insights_total_volume
    val exercisesRes: Int = R.string.insights_exercises
    val updatedJustNowRes: Int = R.string.insights_updated_just_now
    val nothingToShowRes: Int = R.string.insights_nothing_to_show
    val noDataRes: Int = R.string.insights_no_data
    val thisWeekRes: Int = R.string.insights_this_week
    val periodDailyRes: Int = R.string.insights_period_daily
    val periodWeeklyRes: Int = R.string.insights_period_weekly
    val periodMonthlyRes: Int = R.string.insights_period_monthly
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

@Composable
@ReadOnlyComposable
fun InsightsState.getAIOverviewTitle(): String = stringResource(aiOverviewTitleRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getAIOverviewGeneratingText(): String = stringResource(aiOverviewGeneratingRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getLoadingSubtitle(): String = stringResource(loadingSubtitleRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getSessionsLabel(): String = stringResource(sessionsRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getTotalVolumeLabel(): String = stringResource(totalVolumeRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getExercisesLabel(): String = stringResource(exercisesRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getUpdatedJustNow(): String = stringResource(updatedJustNowRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getNothingToShow(): String = stringResource(nothingToShowRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getNoData(): String = stringResource(noDataRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getThisWeek(): String = stringResource(thisWeekRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getDailyLabel(): String = stringResource(periodDailyRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getWeeklyLabel(): String = stringResource(periodWeeklyRes)

@Composable
@ReadOnlyComposable
fun InsightsState.getMonthlyLabel(): String = stringResource(periodMonthlyRes)
