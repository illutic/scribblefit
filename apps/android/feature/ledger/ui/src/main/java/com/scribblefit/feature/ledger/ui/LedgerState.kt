package com.scribblefit.feature.ledger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.model.Workout
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateRangeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
private val workoutHeaderFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())

data class DailyWorkout(
    val date: LocalDate,
    val workouts: List<Workout>,
) {
    val exercises: List<com.scribblefit.core.model.Exercise>
        get() = workouts.sortedBy { it.date }.flatMap { it.exercises }
}

data class LedgerState(
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val workouts: List<Workout> = emptyList(),
    val startDate: LocalDate = LocalDate.now().minusDays(30),
    val endDate: LocalDate = LocalDate.now(),
    val bottomBarState: BottomBarState = BottomBarState(selectedTab = Screen.Ledger),
    val error: Throwable? = null,
) {
    val dateRangeString: String
        get() = "${startDate.format(dateRangeFormatter)} – ${endDate.format(dateRangeFormatter)}"

    val groupedWorkouts: List<DailyWorkout>
        get() = workouts.groupBy { it.date.toLocalDate() }
            .map { (date, workouts) ->
                DailyWorkout(
                    date = date,
                    workouts = workouts.sortedBy { it.date }
                )
            }
            .sortedByDescending { it.date }

    fun getWorkoutDateHeader(date: LocalDate): String = date.format(workoutHeaderFormatter)

    val ledgerTitle: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_title)

    val emptyTitle: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_empty_title)

    val emptyCta: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_empty_cta)

    val loadingText: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_loading)
}

internal fun java.time.Instant.toLocalDate(): LocalDate =
    java.time.LocalDateTime.ofInstant(this, java.time.ZoneId.systemDefault()).toLocalDate()

internal fun Long.toLocalDate(): LocalDate =
    java.time.Instant.ofEpochMilli(this).toLocalDate()
