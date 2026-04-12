package com.scribblefit.feature.ledger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.model.Workout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateRangeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
private val workoutHeaderFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())

data class LedgerState(
    val isLoading: Boolean = false,
    val workouts: List<Workout> = emptyList(),
    val startDate: LocalDate = LocalDate.now().withDayOfMonth(1),
    val endDate: LocalDate = LocalDate.now(),
    val error: Throwable? = null,
) {
    val dateRangeString: String
        get() = "${startDate.format(dateRangeFormatter)} – ${endDate.format(dateRangeFormatter)}"

    val groupedWorkouts: Map<LocalDate, List<Workout>>
        get() = workouts.groupBy { it.date.toLocalDate() }

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

private fun java.time.Instant.toLocalDate(): LocalDate =
    java.time.LocalDateTime.ofInstant(this, java.time.ZoneId.systemDefault()).toLocalDate()

private fun Long.toLocalDate(): LocalDate =
    java.time.Instant.ofEpochMilli(this).toLocalDate()
