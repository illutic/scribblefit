package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val workoutDateFormatter =
    DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())

data class WorkoutExerciseUiModel(
    val id: Long,
    val name: String,
    val formattedSummary: String,
    val estimated1RMValue: Int?,
    val intensityValue: Int?,
    val improvementValue: Int?,
    val hasStats: Boolean,
)

data class WorkoutExercisesState(
    val isLoading: Boolean = true,
    val workoutId: Long = 0,
    val workoutDate: LocalDate? = null,
    val exercises: List<Exercise> = emptyList(),
    val uiModels: List<WorkoutExerciseUiModel> = emptyList(),
    val totalExercises: Int = 0,
    val totalSets: Int = 0,
    val totalVolumeValue: Double = 0.0,
    val totalVolumeDisplay: String = "",
    val isVolumeKilo: Boolean = false,
    val weightUnit: Weight = Weight.KGS,
    val error: String? = null,
) {
    val dateString: String
        get() = workoutDate?.format(workoutDateFormatter) ?: ""

    val totalVolume: String
        @Composable @ReadOnlyComposable
        get() = stringResource(
            R.string.workout_exercises_volume_format,
            totalVolumeDisplay + if (isVolumeKilo) "k" else "",
            weightUnitLabel
        )

    val exerciseUiModels: List<WorkoutExerciseUiModel>
        @Composable @ReadOnlyComposable
        get() = uiModels

    @Composable
    @ReadOnlyComposable
    fun getEstimated1RM(uiModel: WorkoutExerciseUiModel): String? = uiModel.estimated1RMValue?.let {
        stringResource(R.string.workout_exercises_estimated_1rm_format, it, weightUnitLabel)
    }

    @Composable
    @ReadOnlyComposable
    fun getIntensity(uiModel: WorkoutExerciseUiModel): String? = uiModel.intensityValue?.let {
        stringResource(R.string.workout_exercises_intensity_format, it)
    }

    @Composable
    @ReadOnlyComposable
    fun getImprovement(uiModel: WorkoutExerciseUiModel): String? = uiModel.improvementValue?.let {
        val sign = if (it >= 0) "+" else ""
        stringResource(R.string.workout_exercises_improvement_format, "$sign$it", weightUnitLabel)
    }

    val exercisesLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.workout_exercises_exercises_label)

    val setsLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.workout_exercises_sets_label)

    val volumeLabel: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.workout_exercises_volume_label)

    val backContentDescription: String
        @Composable @ReadOnlyComposable
        get() = stringResource(R.string.workout_exercises_back)

    val weightUnitLabel: String
        @Composable @ReadOnlyComposable
        get() = if (weightUnit == Weight.KGS) {
            stringResource(R.string.workout_exercises_weight_unit_kg)
        } else {
            stringResource(R.string.workout_exercises_weight_unit_lb)
        }
}
