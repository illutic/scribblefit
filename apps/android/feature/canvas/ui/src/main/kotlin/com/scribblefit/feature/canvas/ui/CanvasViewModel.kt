package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.domain.ConfirmScribbleUseCase
import com.scribblefit.feature.canvas.domain.DeleteScribbleUseCase
import com.scribblefit.feature.canvas.domain.GetScribblesForDateUseCase
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.AddRawScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ManualEditScribbleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CanvasViewModel @Inject constructor(
    private val getScribblesForDateUseCase: GetScribblesForDateUseCase,
    private val addRawScribbleUseCase: AddRawScribbleUseCase,
    private val confirmScribbleUseCase: ConfirmScribbleUseCase,
    private val deleteScribbleUseCase: DeleteScribbleUseCase,
    private val parsePendingScribblesUseCase: ParsePendingScribblesUseCase,
    private val manualEditScribbleUseCase: ManualEditScribbleUseCase,
    private val getAIInsightsUseCase: GetAIOverviewUseCase,
    private val formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(CanvasState())
    private val currentDateValue get() = _state.value.currentDate
    private val currentDate = _state.map { it.currentDate }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val scribblesForDate = currentDate.flatMapLatest { date ->
        getScribblesForDateUseCase(date)
    }

    private val preferredWeight =
        configRepository.config.map { it.weightUnit }.distinctUntilChanged()

    val state = combine(
        _state,
        preferredWeight,
        scribblesForDate,
        navigator.navState
    ) { currentState, weightUnit, scribbles, navState ->
        currentState.copy(
            weightUnit = weightUnit,
            scribbles = scribbles,
            scribbleUiModels = scribbles.map { scribble ->
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
                            name = exercise.canonicalName,
                            formattedSummary = formatExerciseSummaryUseCase(exercise, weightUnit),
                            estimated1RMValue = exercise.estimated1RM?.toInt(),
                            intensityValue = exercise.intensity?.let { (it * 100).toInt() },
                            improvementValue = exercise.improvement?.toInt(),
                            hasStats = exercise.estimated1RM != null ||
                                    exercise.intensity != null ||
                                    exercise.improvement != null,
                            firstSetWeight = weightValue,
                            totalSets = totalSets,
                            repsPerSet = repsPerSet
                        )
                    },
                    scribble = scribble
                )
            },
            bottomBarState = navState.bottomBarState,
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    init {
        viewModelScope.launch {
            currentDate.collectLatest { date ->
                _state.update { it.copy(isLoading = true) }
                launch { parsePendingScribblesUseCase(date) }
                loadAIInsights(date)
            }
        }
    }

    fun onIntent(intent: CanvasIntent) {
        when (intent) {
            is CanvasIntent.UpdateScribbleText,
            is CanvasIntent.AddScribble,
            is CanvasIntent.RetryScribbleParsing,
            is CanvasIntent.ClickOnScribble,
            is CanvasIntent.ConfirmScribble,
            is CanvasIntent.DeleteScribble,
            is CanvasIntent.ShowDeleteConfirmation,
            CanvasIntent.HideDeleteConfirmation,
            CanvasIntent.DismissScribbleDialog -> handleScribbleIntent(intent)

            CanvasIntent.OnPreviousDayClick,
            CanvasIntent.OnNextDayClick,
            CanvasIntent.ShowDatePicker,
            CanvasIntent.DismissDatePicker,
            is CanvasIntent.OnDateSelected -> handleDateIntent(intent)

            is CanvasIntent.UpdateExerciseName,
            is CanvasIntent.UpdateSetWeight,
            is CanvasIntent.UpdateSetReps,
            is CanvasIntent.DeleteSet -> handleEditIntent(intent)

            CanvasIntent.NavigateBack,
            is CanvasIntent.NavigateToScreen,
            is CanvasIntent.NavigateToExerciseDetails,
            is CanvasIntent.NavigateToWorkoutExercises -> handleNavigationIntent(intent)
        }
    }

    private fun handleScribbleIntent(intent: CanvasIntent) {
        when (intent) {
            is CanvasIntent.UpdateScribbleText -> {
                _state.update { it.copy(currentScribbleText = intent.text) }
            }

            is CanvasIntent.AddScribble -> {
                addScribble(intent.scribble)
            }

            is CanvasIntent.RetryScribbleParsing -> {
                retryScribbleParsing(intent.scribble)
            }

            is CanvasIntent.ClickOnScribble -> {
                scribbleClicked(intent.scribble)
            }

            is CanvasIntent.ConfirmScribble -> {
                confirmScribble(intent.scribble)
            }

            is CanvasIntent.DeleteScribble -> {
                deleteScribble(intent.scribbleId)
                _state.update { it.copy(showDeleteConfirmation = false, deletingScribbleId = null) }
            }

            is CanvasIntent.ShowDeleteConfirmation -> {
                _state.update {
                    it.copy(
                        showDeleteConfirmation = true,
                        deletingScribbleId = intent.scribbleId
                    )
                }
            }

            CanvasIntent.HideDeleteConfirmation -> {
                _state.update { it.copy(showDeleteConfirmation = false, deletingScribbleId = null) }
            }

            CanvasIntent.DismissScribbleDialog -> {
                dismissScribbleDialog()
            }

            else -> {}
        }
    }

    private fun handleDateIntent(intent: CanvasIntent) {
        when (intent) {
            CanvasIntent.OnPreviousDayClick -> {
                updateDate(currentDateValue.minusDays(1))
            }

            CanvasIntent.OnNextDayClick -> {
                val nextDay = currentDateValue.plusDays(1)
                if (!nextDay.isAfter(LocalDate.now())) {
                    updateDate(nextDay)
                }
            }

            CanvasIntent.ShowDatePicker -> {
                _state.update { it.copy(isDatePickerVisible = true) }
            }

            CanvasIntent.DismissDatePicker -> {
                _state.update { it.copy(isDatePickerVisible = false) }
            }

            is CanvasIntent.OnDateSelected -> {
                if (!intent.date.isAfter(LocalDate.now())) {
                    updateDate(intent.date)
                }
                _state.update { it.copy(isDatePickerVisible = false) }
            }

            else -> {}
        }
    }

    private fun handleEditIntent(intent: CanvasIntent) {
        when (intent) {
            is CanvasIntent.UpdateExerciseName -> {
                updateExerciseName(intent.exerciseId, intent.newName)
            }

            is CanvasIntent.UpdateSetWeight -> {
                updateSetWeight(intent.exerciseId, intent.setId, intent.newWeight)
            }

            is CanvasIntent.UpdateSetReps -> {
                updateSetReps(intent.exerciseId, intent.setId, intent.newReps)
            }

            is CanvasIntent.DeleteSet -> {
                deleteSet(intent.exerciseId, intent.setId)
            }

            else -> {}
        }
    }

    private fun handleNavigationIntent(intent: CanvasIntent) {
        when (intent) {
            CanvasIntent.NavigateBack -> {
                navigator.goBack()
            }

            is CanvasIntent.NavigateToScreen -> {
                navigator.navigateTo(intent.screen)
            }

            is CanvasIntent.NavigateToExerciseDetails -> {
                navigator.navigateTo(Screen.ExerciseDetails(intent.exerciseName))
            }

            is CanvasIntent.NavigateToWorkoutExercises -> {
                navigator.navigateTo(Screen.WorkoutExercises(intent.workoutId))
            }

            else -> {}
        }
    }

    private fun addScribble(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            addRawScribbleUseCase(text, currentDateValue)
            _state.update { it.copy(currentScribbleText = "") }
        }
    }

    private fun retryScribbleParsing(scribble: Scribble) {
        viewModelScope.launch {
            parsePendingScribblesUseCase.parseSingleScribble(scribble)
        }
    }

    private fun scribbleClicked(scribble: Scribble) {
        when (scribble.status) {
            ScribbleStatus.SUCCESS -> _state.update {
                it.copy(selectedScribble = scribble)
            }

            ScribbleStatus.COMPLETED -> {
                val workoutId = scribble.workoutId ?: return
                navigator.navigateTo(Screen.WorkoutExercises(workoutId = workoutId))
            }

            else -> {}
        }
    }

    private fun updateDate(date: LocalDate) {
        _state.update { it.copy(currentDate = date) }
    }

    private fun confirmScribble(scribble: Scribble) {
        viewModelScope.launch {
            confirmScribbleUseCase(scribble)
            dismissScribbleDialog()
        }
    }

    private fun deleteScribble(id: Long) {
        viewModelScope.launch {
            deleteScribbleUseCase(id)
            dismissScribbleDialog()
        }
    }

    private fun dismissScribbleDialog() {
        _state.update { it.copy(selectedScribble = null) }
    }

    private fun updateExerciseName(exerciseId: Long, newName: String) {
        val scribble = _state.value.selectedScribble ?: return
        viewModelScope.launch {
            manualEditScribbleUseCase.updateExerciseName(scribble.id, exerciseId, newName)
        }
    }

    private fun updateSetWeight(exerciseId: Long, setId: Long, newWeight: String) {
        val scribble = _state.value.selectedScribble ?: return
        val weight = newWeight.toFloatOrNull() ?: return
        viewModelScope.launch {
            manualEditScribbleUseCase.updateSetWeight(scribble.id, exerciseId, setId, weight)
        }
    }

    private fun updateSetReps(exerciseId: Long, setId: Long, newReps: String) {
        val scribble = _state.value.selectedScribble ?: return
        val reps = newReps.toIntOrNull() ?: return
        viewModelScope.launch {
            manualEditScribbleUseCase.updateSetReps(scribble.id, exerciseId, setId, reps)
        }
    }

    private fun deleteSet(exerciseId: Long, setId: Long) {
        val scribble = _state.value.selectedScribble ?: return
        viewModelScope.launch {
            manualEditScribbleUseCase.deleteSet(scribble.id, exerciseId, setId)
        }
    }

    private fun loadAIInsights(date: LocalDate) {
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingInsights = true) }
            val insights = getAIInsightsUseCase(date)
            _state.update {
                it.copy(
                    isGeneratingInsights = false,
                    aiInsights = insights.getOrDefault(emptyList())
                )
            }
        }
    }
}
