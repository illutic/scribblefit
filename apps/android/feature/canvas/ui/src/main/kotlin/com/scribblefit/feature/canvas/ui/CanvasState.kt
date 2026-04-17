package com.scribblefit.feature.canvas.ui

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.BottomBarState
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.current.platformLocale)

data class ExerciseUiModel(
    val id: Long,
    val summary: String,
    val estimated1RM: String?,
    val intensity: String?,
    val improvement: String?,
    val hasStats: Boolean
)

data class ScribbleUiModel(
    val id: Long,
    val rawText: String,
    val status: ScribbleStatus,
    val exercises: List<ExerciseUiModel>,
    val statusText: String? = null,
    val scribble: Scribble,
)

data class CanvasState(
    val isLoading: Boolean = false,
    val currentDate: LocalDate = LocalDate.now(),
    val error: Throwable? = null,
    val currentScribbleText: String = "",
    val scribbles: List<Scribble> = emptyList(),
    val selectedScribble: Scribble? = null,
    val bottomBarState: BottomBarState = BottomBarState(),
    val aiInsights: List<com.scribblefit.core.model.AIInsight> = emptyList(),
    val isGeneratingInsights: Boolean = false,
    val isDatePickerVisible: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
) {
    val isCurrentDate = currentDate == LocalDate.now()
    val dateString: String by lazy { currentDate.format(dateFormatter) }

    val scribbleUiModels: List<ScribbleUiModel>
        @Composable @ReadOnlyComposable
        get() = scribbles.map { scribble ->
            ScribbleUiModel(
                id = scribble.id,
                rawText = scribble.rawText,
                status = scribble.status,
                exercises = scribble.exercises.map { exercise ->
                    val firstSet = exercise.sets.firstOrNull()
                    val totalSets = exercise.sets.size
                    val repsPerSet = firstSet?.reps ?: 0
                    val weightValue = firstSet?.weight ?: 0f

                    ExerciseUiModel(
                        id = exercise.id,
                        summary = stringResource(
                            R.string.canvas_workout_summary_format,
                            exercise.canonicalName,
                            weightValue,
                            weightUnitLabel,
                            totalSets,
                            repsPerSet
                        ),
                        estimated1RM = exercise.estimated1RM?.let {
                            stringResource(
                                R.string.canvas_estimated_1rm_value_format,
                                it.toInt(),
                                weightUnitLabel
                            )
                        },
                        intensity = exercise.intensity?.let {
                            stringResource(
                                R.string.canvas_intensity_value_format,
                                (it * 100).toInt()
                            )
                        },
                        improvement = exercise.improvement?.let {
                            val sign = if (it >= 0) "+" else ""
                            stringResource(
                                R.string.canvas_last_session_improvement_format,
                                "$sign${it.toInt()}",
                                weightUnitLabel
                            )
                        },
                        hasStats = exercise.estimated1RM != null || exercise.intensity != null || exercise.improvement != null
                    )
                },
                statusText = when (scribble.status) {
                    ScribbleStatus.PENDING, ScribbleStatus.PARSING -> parsingWorkoutText
                    ScribbleStatus.FAILED -> failedToParseText
                    ScribbleStatus.SUCCESS -> tapToConfirmText
                    else -> null
                },
                scribble = scribble
            )
        }

    val emptyScribbleText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_empty_scribble_text)

    val errorMessage: String
        @Composable @ReadOnlyComposable
        get() =
            error?.localizedMessage
                ?: stringResource(R.string.canvas_error_unknown)

    val textfieldPlaceholder: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_textfield_placeholder)

    val appName: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_app_name)

    val aiInsightsLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_ai_insights)

    val closeContentDescription: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_close)

    val parsingWorkoutText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_parsing_workout_data)

    val tapToConfirmText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_tap_to_confirm)

    val loggedLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_logged)

    val estimated1rmLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_estimated_1rm)

    val intensityLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_intensity)

    val failedToParseText: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_failed_to_parse_workout)

    val editLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_edit)

    val retryLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_retry)

    val removeLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_remove)

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

    val collapseContentDescription: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_collapse)

    val expandSearchContentDescription: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.canvas_expand_search)

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
