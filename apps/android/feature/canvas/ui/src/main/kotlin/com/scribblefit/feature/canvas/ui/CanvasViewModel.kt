package com.scribblefit.feature.canvas.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.canvas.domain.AddScribbleUseCase
import com.scribblefit.feature.canvas.domain.ConfirmScribbleUseCase
import com.scribblefit.feature.canvas.domain.DeleteScribbleUseCase
import com.scribblefit.feature.canvas.domain.GetScribblesForDateUseCase
import com.scribblefit.feature.canvas.domain.ParsePendingScribblesUseCase
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.scribble.domain.usecase.EditScribbleUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase
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
class CanvasViewModel
@Inject
constructor(
    private val getScribblesForDateUseCase: GetScribblesForDateUseCase,
    private val parsePendingScribblesUseCase: ParsePendingScribblesUseCase,
    private val addScribbleUseCase: AddScribbleUseCase,
    private val editScribbleUseCase: EditScribbleUseCase,
    private val confirmScribbleUseCase: ConfirmScribbleUseCase,
    private val deleteScribbleUseCase: DeleteScribbleUseCase,
    private val getAIOverviewUseCase: GetAIOverviewUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator,
    private val removeSetUseCase: RemoveSetUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CanvasState())
    private val currentDate = _state.map { it.currentDate }.distinctUntilChanged()
    @OptIn(ExperimentalCoroutinesApi::class)
    private val scribblesForDate = currentDate.flatMapLatest { date ->
        _state.update { it.copy(isLoading = true) }
        getScribblesForDateUseCase(date).also {
            _state.update { it.copy(isLoading = false) }
        }
    }
    private val preferredWeight = configRepository.config.map { it.weightUnit }.distinctUntilChanged()
    private val aiInsights = currentDate.map { date ->
        _state.update { it.copy(isGeneratingInsights = true) }
        getAIOverviewUseCase(date).also {
            _state.update { it.copy(isGeneratingInsights = false) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Result.success(emptyList()))

    val state = combine(
        _state,
        preferredWeight,
        scribblesForDate,
        aiInsights
    ) { currentState, weightUnit, scribbles, aiInsights ->
        currentState.copy(
            weightUnit = weightUnit,
            scribbles = scribbles,
            aiInsights = aiInsights.getOrNull() ?: emptyList(),
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    init {
        viewModelScope.launch {
            currentDate.collectLatest {
                parsePendingScribblesUseCase(_state.value.currentDate)
            }
        }
    }

    fun onIntent(intent: CanvasIntent) {
        when (intent) {
            is CanvasIntent.UpdateScribbleText -> {
                _state.update { it.copy(currentScribbleText = intent.text) }
            }

            is CanvasIntent.AddScribble -> {
                addScribble(intent.scribble)
            }

            is CanvasIntent.RetryScribbleParsing -> {
                viewModelScope.launch {
                    parsePendingScribblesUseCase.parseSingleScribble(intent.scribble)
                }
            }

            CanvasIntent.ToggleInputExpansion -> {
                _state.update { it.copy(isInputExpanded = !it.isInputExpanded) }
            }

            is CanvasIntent.NavigateToScreen -> {
                navigator.navigateTo(intent.screen)
            }

            is CanvasIntent.ClickOnScribble -> {
                scribbleClicked(intent.scribble)
            }

            is CanvasIntent.ConfirmScribble -> {
                confirmScribble(intent.scribble)
            }

            is CanvasIntent.DeleteScribble -> {
                deleteScribble(intent.scribbleId)
            }

            CanvasIntent.DismissScribbleDialog -> {
                dismissScribbleDialog()
            }

            CanvasIntent.NavigateBack -> {
                navigator.goBack()
            }

            is CanvasIntent.UpdateScribble -> {
                editScribble(intent.scribble)
            }

            CanvasIntent.OnPreviousDayClick -> {
                val prevDate = _state.value.currentDate.minusDays(1)
                _state.update { it.copy(currentDate = prevDate) }
            }

            CanvasIntent.OnNextDayClick -> {
                val nextDate = _state.value.currentDate.plusDays(1)
                if (!nextDate.isAfter(LocalDate.now())) {
                    _state.update { it.copy(currentDate = nextDate) }
                }
            }

            CanvasIntent.ShowDatePicker -> {
                _state.update { it.copy(isDatePickerVisible = true) }
            }

            CanvasIntent.DismissDatePicker -> {
                _state.update { it.copy(isDatePickerVisible = false) }
            }

            is CanvasIntent.OnDateSelected -> {
                val today = LocalDate.now()
                if (!intent.date.isAfter(today)) {
                    _state.update {
                        it.copy(
                            currentDate = intent.date,
                            isDatePickerVisible = false
                        )
                    }
                }
            }

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
        }
    }

    private fun deleteSet(exerciseId: Long, setId: Long) {
        _state.update { currentState ->
            val selectedScribble = currentState.selectedScribble ?: return@update currentState
            
            // If COMPLETED, delete from database too
            if (selectedScribble.status == ScribbleStatus.COMPLETED) {
                viewModelScope.launch {
                    removeSetUseCase(setId)
                }
            }

            val updatedExercises = selectedScribble.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val updatedSets = exercise.sets
                        .filterNot { it.id == setId }
                        .mapIndexed { index, set -> set.copy(setNumber = index + 1) }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
            currentState.copy(selectedScribble = selectedScribble.copy(exercises = updatedExercises))
        }
    }

    private fun updateExerciseName(exerciseId: Long, newName: String) {
        _state.update { currentState ->
            val selectedScribble = currentState.selectedScribble ?: return@update currentState
            val updatedExercises = selectedScribble.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    exercise.copy(canonicalName = newName)
                } else {
                    exercise
                }
            }
            currentState.copy(selectedScribble = selectedScribble.copy(exercises = updatedExercises))
        }
    }

    private fun updateSetWeight(exerciseId: Long, setId: Long, newWeight: String) {
        val weight = newWeight.toFloatOrNull() ?: return
        _state.update { currentState ->
            val selectedScribble = currentState.selectedScribble ?: return@update currentState
            val updatedExercises = selectedScribble.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) {
                            set.copy(weight = weight)
                        } else {
                            set
                        }
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
            currentState.copy(selectedScribble = selectedScribble.copy(exercises = updatedExercises))
        }
    }

    private fun updateSetReps(exerciseId: Long, setId: Long, newReps: String) {
        val reps = newReps.toIntOrNull() ?: return
        _state.update { currentState ->
            val selectedScribble = currentState.selectedScribble ?: return@update currentState
            val updatedExercises = selectedScribble.exercises.map { exercise ->
                if (exercise.id == exerciseId) {
                    val updatedSets = exercise.sets.map { set ->
                        if (set.id == setId) {
                            set.copy(reps = reps)
                        } else {
                            set
                        }
                    }
                    exercise.copy(sets = updatedSets)
                } else {
                    exercise
                }
            }
            currentState.copy(selectedScribble = selectedScribble.copy(exercises = updatedExercises))
        }
    }

    private fun addScribble(text: String) {
        if (text.isBlank()) return
        val editingId = _state.value.editingScribbleId
        viewModelScope.launch {
            if (editingId != null) {
                editScribbleUseCase(editingId, text)
            } else {
                addScribbleUseCase(text, _state.value.currentDate)
            }
            _state.update {
                it.copy(
                    currentScribbleText = "",
                    editingScribbleId = null
                )
            }
        }
    }

    private fun scribbleClicked(scribble: Scribble) {
        when (scribble.status) {
            ScribbleStatus.FAILED -> {} // TODO - Introduce a mechanism to selectively retry parsing scribbles.
            ScribbleStatus.SUCCESS, ScribbleStatus.COMPLETED -> _state.update { it.copy(selectedScribble = scribble) }
            else -> {}
        }
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

    private fun editScribble(scribble: Scribble) {
        _state.update {
            it.copy(
                currentScribbleText = scribble.rawText,
                editingScribbleId = scribble.id,
                selectedScribble = null,
                isInputExpanded = true
            )
        }
    }
}
