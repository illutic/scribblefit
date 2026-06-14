package com.scribblefit.feature.ledger.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val dateRangeFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault())
private val workoutHeaderFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
private val timeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault())

data class DailyScribbles(
    val date: LocalDateTime,
    val scribbles: List<Scribble>
)

data class LedgerState(
    val isLoading: Boolean = false,
    val showDatePicker: Boolean = false,
    val scribbles: List<Scribble> = emptyList(),
    val selectedScribble: Scribble? = null,
    val startDate: LocalDateTime = LocalDateTime.now().minusDays(30).toLocalDate().atStartOfDay(),
    val endDate: LocalDateTime = LocalDateTime.now().toLocalDate().atTime(23, 59, 59, 999999999),
    val weightUnit: Weight = Weight.KGS,
    val bottomBarState: BottomBarState = BottomBarState(selectedTab = Screen.Ledger),
    val error: Throwable? = null,
) {
    val dateRangeString: String
        get() = "${startDate.format(dateRangeFormatter)} – ${endDate.format(dateRangeFormatter)}"

    val groupedScribbles: List<DailyScribbles>
        get() = scribbles
            .groupBy { it.createdAt.toLocalDateTime().toLocalDate().atStartOfDay() }
            .map { (date, scribbles) ->
                DailyScribbles(
                    date = date,
                    scribbles = scribbles.sortedBy { it.createdAt }
                )
            }
            .sortedByDescending { it.date }

    fun getDateHeader(date: LocalDateTime): String = date.format(workoutHeaderFormatter)

    val weightUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = if (weightUnit == Weight.KGS) {
            stringResource(R.string.ledger_weight_unit_kg)
        } else {
            stringResource(R.string.ledger_weight_unit_lb)
        }

    val ledgerTitle: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_title)

    val emptyTitle: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_empty_title)

    val emptyCta: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_empty_cta)

    val loadingText: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_loading)

    val scribbleBadgeLabel: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_scribble_badge)

    val scribbleDetailsTitle: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_scribble_details_title)

    val exercisesLabel: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_scribble_details_exercises_label)

    val loggedLabel: String
        @Composable @ReadOnlyComposable get() = stringResource(R.string.ledger_scribble_details_logged)

    fun formatExerciseSummary(exercise: Exercise): String {
        if (exercise.sets.isEmpty()) return ""
        val unitLabel = if (weightUnit == Weight.KGS) "kg" else "lb"
        val totalSets = exercise.sets.size
        val totalReps = exercise.sets.sumOf { it.reps }
        val maxWeight = exercise.sets.mapNotNull { it.weight }.maxOrNull()
        return if (maxWeight != null) {
            "%.1f%s • %d sets x %d reps".format(maxWeight, unitLabel, totalSets, totalReps)
        } else {
            "%d sets • %d reps".format(totalSets, totalReps)
        }
    }
}

internal fun Long.toLocalDateTime(): LocalDateTime =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

internal fun Long.toTimeString(): String =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .format(timeFormatter)
