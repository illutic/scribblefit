package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.scribble.domain.usecase.AddScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.ConfirmScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesForDateUseCase
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateTrendsUseCase
import com.scribblefit.feature.exercises.domain.usecase.FormatExerciseSummaryUseCase
import com.scribblefit.feature.exercises.domain.usecase.RemoveExerciseUseCase
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.CreateManualScribbleUseCase
import com.scribblefit.feature.scribble.domain.usecase.RemoveScribbleUseCase
import com.scribblefit.feature.sets.domain.usecase.AddSetToExerciseUseCase
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
    private val addScribbleUseCase: AddScribbleUseCase,
    private val confirmScribbleUseCase: ConfirmScribbleUseCase,
    private val deleteScribbleUseCase: RemoveScribbleUseCase,
    private val updateExerciseUseCase: UpdateExerciseUseCase,
    private val removeExerciseUseCase: RemoveExerciseUseCase,
    private val addSetToExerciseUseCase: AddSetToExerciseUseCase,
    private val parsePendingScribblesUseCase: ParsePendingScribblesUseCase,
    private val createManualScribbleUseCase: CreateManualScribbleUseCase,
    private val getAIInsightsUseCase: GetAIOverviewUseCase,
    private val formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase,
    private val calculateTrendsUseCase: CalculateTrendsUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(CanvasState())
    private val currentDateValue get() = _state.value.currentDate
    private val currentDate = _state.map { CurrentDate(it.currentDate) }.distinctUntilChanged()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val scribblesForDate = currentDate.flatMapLatest { date ->
        getScribblesForDateUseCase(date)
    }

    private val preferredWeight =
        configRepository.config.map { it.localConfig.weightUnit }.distinctUntilChanged()

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
                        val trends = calculateTrendsUseCase(exercise.id)

                        ExerciseUiModel(
                            id = exercise.id,
                            name = exercise.canonicalName,
                            formattedSummary = formatExerciseSummaryUseCase(exercise, weightUnit),
                            firstSetWeight = weightValue,
                            totalSets = totalSets,
                            repsPerSet = repsPerSet,
                            estimated1RMValue = trends.getOrNull()?.estimated1RM,
                            intensityValue = trends.getOrNull()?.intensity,
                            improvementValue = trends.getOrNull()?.improvement,
                            hasStats = trends.getOrNull() != null,
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
            is CanvasIntent.DeleteExercise,
            is CanvasIntent.AddSet,
            is CanvasIntent.SaveManualExercise,
            is CanvasIntent.ShowAddExerciseSheet,
            is CanvasIntent.HideAddExerciseSheet,
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

            is CanvasIntent.DeleteExercise -> {
                deleteExercise(intent.exerciseId)
            }

            is CanvasIntent.AddSet -> {
                addSet(intent.exerciseId)
            }

            CanvasIntent.ShowAddExerciseSheet -> {
                _state.update { it.copy(isAddExerciseSheetVisible = true) }
            }

            CanvasIntent.HideAddExerciseSheet -> {
                _state.update { it.copy(isAddExerciseSheetVisible = false) }
            }

            is CanvasIntent.SaveManualExercise -> {
                saveManualExercise(intent.name, intent.muscleGroup, intent.sets)
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
                navigator.navigateTo(Screen.ExerciseDetails(intent.exerciseId))
            }

            is CanvasIntent.NavigateToWorkoutExercises -> {
                navigator.navigateTo(Screen.Ledger)
            }

            else -> {}
        }
    }

    private fun addScribble(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            addScribbleUseCase(text, CurrentDate(currentDateValue))
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
        viewModelScope.launch {
            val scribble = _state.value.selectedScribble ?: return@launch
            val exercise = scribble.exercises.find { it.id == exerciseId } ?: return@launch
            updateExerciseUseCase(exercise.copy(canonicalName = newName))
        }
    }

    private fun updateSetWeight(exerciseId: Long, setId: Long, newWeight: String) {
        viewModelScope.launch {
            val scribble = _state.value.selectedScribble ?: return@launch
            val weight = newWeight.toFloatOrNull() ?: return@launch
            val exercise = scribble.exercises.find { it.id == exerciseId } ?: return@launch
            val newExercise = exercise.copy(
                sets = exercise.sets.map {
                    if (it.id == setId) it.copy(weight = weight) else it
                }
            )

            updateExerciseUseCase(newExercise)
        }
    }

    private fun updateSetReps(exerciseId: Long, setId: Long, newReps: String) {
        viewModelScope.launch {
            val scribble = _state.value.selectedScribble ?: return@launch
            val reps = newReps.toIntOrNull() ?: return@launch
            val exercise = scribble.exercises.find { it.id == exerciseId } ?: return@launch
            val newExercise = exercise.copy(
                sets = exercise.sets.map {
                    if (it.id == setId) it.copy(reps = reps) else it
                }
            )

            updateExerciseUseCase(newExercise)
        }
    }

    private fun deleteSet(exerciseId: Long, setId: Long) {
        viewModelScope.launch {
            val scribble = _state.value.selectedScribble ?: return@launch
            val exercise = scribble.exercises.find { it.id == exerciseId } ?: return@launch
            val newExercise = exercise.copy(
                sets = exercise.sets.filterNot { it.id == setId }
            )

            updateExerciseUseCase(newExercise)
        }
    }

    private fun deleteExercise(exerciseId: Long) {
        viewModelScope.launch {
            removeExerciseUseCase(exerciseId)
        }
    }

    private fun addSet(exerciseId: Long) {
        viewModelScope.launch {
            val scribble = _state.value.selectedScribble ?: return@launch
            val exercise = scribble.exercises.find { it.id == exerciseId } ?: return@launch
            addSetToExerciseUseCase(exercise)
        }
    }

    private fun saveManualExercise(
        name: String,
        muscleGroup: String,
        sets: List<Set>
    ) {
        viewModelScope.launch {
            val date = CurrentDate(_state.value.currentDate)
            createManualScribbleUseCase(name, muscleGroup, sets, date).onSuccess {
                _state.update { it.copy(isAddExerciseSheetVisible = false) }
            }
        }
    }

    private fun loadAIInsights(date: CurrentDate) {
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
