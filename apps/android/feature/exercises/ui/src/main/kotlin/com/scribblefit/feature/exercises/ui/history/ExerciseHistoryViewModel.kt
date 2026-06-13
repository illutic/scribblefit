package com.scribblefit.feature.exercises.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ExerciseHistoryViewModel @Inject constructor(
    private val getExerciseHistoryUseCase: GetExerciseHistoryUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseHistoryState())
    val state: StateFlow<ExerciseHistoryState> = _state.asStateFlow()

    private val headerFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())

    fun onIntent(intent: ExerciseHistoryIntent) {
        when (intent) {
            is ExerciseHistoryIntent.LoadHistory -> loadHistory(intent.exerciseName)
            is ExerciseHistoryIntent.NavigateToScribble -> {
                val localDate =
                    Instant.ofEpochMilli(intent.sessionDate).atZone(ZoneId.systemDefault())
                        .toLocalDate()
                navigator.navigateTo(
                    Screen.Canvas(
                        dateEpochDays = localDate.toEpochDay(),
                        isTop = false
                    )
                )
            }

            ExerciseHistoryIntent.NavigateBack -> navigator.goBack()
        }
    }

    private fun loadHistory(exerciseName: String) {
        viewModelScope.launch {
            _state.update { it.copy(exerciseName = exerciseName, isLoading = true, error = null) }
            val weightUnit = configRepository.config.value.localConfig.weightUnit

            getExerciseHistoryUseCase(exerciseName, weightUnit).fold(
                onSuccess = { history ->
                    _state.update { state ->
                        state.copy(
                            history = history,
                            isLoading = false,
                            error = null
                        )
                    }
                },
                onFailure = { err ->
                    _state.update { it.copy(error = err.message, isLoading = false) }
                }
            )
        }
    }
}
