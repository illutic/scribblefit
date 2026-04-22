package com.scribblefit.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.workouts.domain.usecase.CalculateWorkoutVolumeUseCase
import com.scribblefit.feature.workouts.domain.usecase.FormatWorkoutSummaryUseCase
import com.scribblefit.feature.workouts.domain.usecase.GetWorkoutWithExercisesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class WorkoutExercisesViewModel @Inject constructor(
    private val getWorkoutWithExercisesUseCase: GetWorkoutWithExercisesUseCase,
    private val calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase,
    private val formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase,
    private val formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val workoutIdFlow = MutableStateFlow<Long?>(null)
    private val _state = MutableStateFlow(WorkoutExercisesState())

    private val preferredWeight =
        configRepository.config.map { it.weightUnit }.distinctUntilChanged()

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state = combine(
        _state,
        preferredWeight,
        workoutIdFlow.flatMapLatest { id ->
            if (id == null) kotlinx.coroutines.flow.flowOf(null)
            else getWorkoutWithExercisesUseCase(id)
        }
    ) { currentState, weightUnit, workout ->
        if (workout == null) {
            currentState.copy(
                weightUnit = weightUnit,
                isLoading = workoutIdFlow.value != null
            )
        } else {
            val totalVolumeValue = calculateWorkoutVolumeUseCase(workout)
            val volumeSummary = formatWorkoutSummaryUseCase(totalVolumeValue)
            
            currentState.copy(
                isLoading = false,
                workoutDate = epochMillisToLocalDate(workout.date),
                exercises = workout.exercises,
                uiModels = workout.exercises.map { exercise ->
                    WorkoutExerciseUiModel(
                        id = exercise.id,
                        name = exercise.canonicalName,
                        formattedSummary = formatExerciseSummaryUseCase(exercise, weightUnit),
                        estimated1RMValue = exercise.estimated1RM?.toInt(),
                        intensityValue = exercise.intensity?.let { (it * 100).toInt() },
                        improvementValue = exercise.improvement?.toInt(),
                        hasStats = exercise.estimated1RM != null || exercise.intensity != null || exercise.improvement != null
                    )
                },
                totalExercises = workout.exercises.size,
                totalSets = workout.exercises.sumOf { it.sets.size },
                totalVolumeValue = totalVolumeValue,
                totalVolumeDisplay = volumeSummary.value,
                isVolumeKilo = volumeSummary.isKilo,
                weightUnit = weightUnit
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    fun loadWorkout(workoutId: Long) {
        workoutIdFlow.value = workoutId
    }

    fun onIntent(intent: WorkoutExercisesIntent) {
        when (intent) {
            is WorkoutExercisesIntent.ExerciseClicked -> {
                navigator.navigateTo(Screen.ExerciseDetails(exerciseName = intent.exerciseName))
            }

            WorkoutExercisesIntent.NavigateBack -> {
                navigator.goBack()
            }
        }
    }

    private fun epochMillisToLocalDate(epochMillis: Long): LocalDate =
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()).toLocalDate()
}
