package com.scribblefit.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseTrendDataUseCase
import com.scribblefit.feature.exercises.domain.usecase.TrendMetric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExerciseTrendsViewModel @Inject constructor(
    private val getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase,
    configRepository: ConfigRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _state = MutableStateFlow(ExerciseTrendsState())
    val state: StateFlow<ExerciseTrendsState> = combine(
        _state,
        configRepository.config,
        navigator.navState
    ) { state, config, navState ->
        _state.update { it.copy(isLoading = true) }

        val exerciseName = (navState.backStack.lastOrNull() as? Screen.ExerciseTrends)?.exerciseName
            ?: return@combine state.copy(
                isLoading = false,
                error = "Exercise name not found in navigation state."
            )

        val period = state.selectedPeriod
        val oneRM = getExerciseTrendDataUseCase(exerciseName, TrendMetric.ONE_RM, period)
        val volume = getExerciseTrendDataUseCase(exerciseName, TrendMetric.VOLUME, period)

        state.copy(
            isLoading = false,
            weightUnit = config.localConfig.weightUnit,
            exerciseName = exerciseName,
            oneRMDataPoints = oneRM.getOrNull()?.dataPoints ?: emptyList(),
            oneRMInsights = oneRM.getOrNull()?.insights,
            volumeDataPoints = volume.getOrNull()?.dataPoints ?: emptyList(),
            volumeInsights = volume.getOrNull()?.insights,
            error = oneRM.exceptionOrNull()?.message ?: volume.exceptionOrNull()?.message
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), _state.value)

    fun onIntent(intent: ExerciseTrendsIntent) {
        when (intent) {
            is ExerciseTrendsIntent.UpdatePeriod -> {
                _state.update { it.copy(selectedPeriod = intent.period) }
            }

            ExerciseTrendsIntent.NavigateBack -> navigator.goBack()
        }
    }
}
