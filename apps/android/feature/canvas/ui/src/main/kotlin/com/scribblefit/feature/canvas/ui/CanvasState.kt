package com.scribblefit.feature.canvas.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.BottomBarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.current.platformLocale)

data class ExerciseUiModel(
    val id: Long,
    val name: String,
    val formattedSummary: String,
    val estimated1RMValue: Int?,
    val intensityValue: Int?,
    val improvementValue: Int?,
    val hasStats: Boolean,
    val firstSetWeight: Float,
    val totalSets: Int,
    val repsPerSet: Int
)

data class ScribbleUiModel(
    val id: Long,
    val rawText: String,
    val status: ScribbleStatus,
    val exercises: List<ExerciseUiModel>,
    val scribble: Scribble,
)

data class CanvasState(
    val isLoading: Boolean = false,
    val currentDate: LocalDate = LocalDate.now(),
    val error: Throwable? = null,
    val currentScribbleText: String = "",
    val scribbles: List<Scribble> = emptyList(),
    val scribbleUiModels: List<ScribbleUiModel> = emptyList(),
    val selectedScribble: Scribble? = null,
    val bottomBarState: BottomBarState = BottomBarState(),
    val aiInsights: List<com.scribblefit.core.model.AIInsight> = emptyList(),
    val isGeneratingInsights: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
    val showDeleteConfirmation: Boolean = false,
    val deletingScribbleId: Long? = null,
) {
    val dateString: String by lazy { currentDate.format(dateFormatter) }

    val uiModels: List<ScribbleUiModel>
        @Composable @ReadOnlyComposable
        get() = scribbleUiModels

    @Composable
    @ReadOnlyComposable
    fun getExerciseSummary(exercise: ExerciseUiModel): String = stringResource(
        R.string.canvas_workout_summary_format,
        exercise.name,
        exercise.firstSetWeight,
        weightUnitLabel,
        exercise.totalSets,
        exercise.repsPerSet
    )

    @Composable
    @ReadOnlyComposable
    fun getEstimated1RM(exercise: ExerciseUiModel): String? = exercise.estimated1RMValue?.let {
        stringResource(R.string.canvas_estimated_1rm_value_format, it, weightUnitLabel)
    }

    @Composable
    @ReadOnlyComposable
    fun getIntensity(exercise: ExerciseUiModel): String? = exercise.intensityValue?.let {
        stringResource(R.string.canvas_intensity_value_format, it)
    }

    @Composable
    @ReadOnlyComposable
    fun getImprovement(exercise: ExerciseUiModel): String? = exercise.improvementValue?.let {
        val sign = if (it >= 0) "+" else ""
        stringResource(
            R.string.canvas_last_session_improvement_label_format,
            "$sign$it",
            weightUnitLabel
        )
    }

    val estimated1RMLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.canvas_estimated_1rm)
    val intensityLabel: String @Composable @ReadOnlyComposable get() = stringResource(R.string.canvas_intensity)

    @Composable
    @ReadOnlyComposable
    fun getStatusText(status: ScribbleStatus): String? = when (status) {
        ScribbleStatus.PENDING, ScribbleStatus.PARSING -> parsingWorkoutText
        ScribbleStatus.FAILED -> failedToParseText
        ScribbleStatus.SUCCESS -> tapToConfirmText
        else -> null
    }

    @Composable
    @ReadOnlyComposable
    fun getBadgeText(status: ScribbleStatus): String = when (status) {
        ScribbleStatus.PENDING -> stringResource(R.string.canvas_status_pending)
        ScribbleStatus.PARSING -> stringResource(R.string.canvas_status_parsing)
        ScribbleStatus.SUCCESS -> stringResource(R.string.canvas_parsed)
        ScribbleStatus.COMPLETED -> stringResource(R.string.canvas_logged)
        ScribbleStatus.FAILED -> stringResource(R.string.canvas_status_failed)
    }

    val retryActionLabel: String @Composable @ReadOnlyComposable get() = retryActionText
    val removeActionLabel: String @Composable @ReadOnlyComposable get() = removeActionText

    val emptyScribbleText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_empty_scribble_text)

    val textfieldPlaceholder: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_textfield_placeholder)

    val tapToConfirmActionText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_tap_to_confirm_action)

    val retryActionText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_retry_action)

    val removeActionText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_remove_action)

    val parsingWorkoutText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_parsing_workout_data)

    val tapToConfirmText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_tap_to_confirm)

    val failedToParseText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_failed_to_parse_workout)

    val setLabelFormat: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_set_label)

    val repsLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_reps_label)

    val setRepsSeparator: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_set_reps_separator)

    val deleteSetContentDescription: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_delete_set_content_description)

    val deleteDialogTitle: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_delete_dialog_title)

    val deleteDialogText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_delete_dialog_text)

    val deleteConfirmLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_delete_confirm)

    val deleteCancelLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_delete_cancel)

    val weightUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = if (weightUnit == Weight.KGS) {
            stringResource(R.string.canvas_weight_unit_kg)
        } else {
            stringResource(R.string.canvas_weight_unit_lb)
        }

    val isLandscape: Boolean
        @Composable @ReadOnlyComposable get() = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
}
