package com.scribblefit.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.CalculateTrendsUseCase
import com.scribblefit.feature.exercises.domain.usecase.CalculateWeeklyStatsUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseAIInsightUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    private val calculateWeeklyStatsUseCase: CalculateWeeklyStatsUseCase,
    private val calculateTrendsUseCase: CalculateTrendsUseCase,
    private val getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
    private val getExerciseByIdUseCase: GetExerciseByIdUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseDetailsState())
    val state: StateFlow<ExerciseDetailsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            configRepository.config.collect { config ->
                _state.update { it.copy(weightUnit = config.localConfig.weightUnit) }
            }
        }
    }

    fun onIntent(intent: ExerciseDetailsIntent) {
        when (intent) {
            is ExerciseDetailsIntent.LoadDetails -> loadExercise(intent.exerciseId)
            ExerciseDetailsIntent.RefreshAIInsight -> refreshAIInsight()
            ExerciseDetailsIntent.NavigateBack -> navigator.goBack()
            ExerciseDetailsIntent.NavigateToTrends -> {
                navigator.navigateTo(Screen.ExerciseTrends(state.value.exerciseName))
            }
            ExerciseDetailsIntent.NavigateToHistory -> {
                navigator.navigateTo(Screen.ExerciseHistory(state.value.exerciseName))
            }
        }
    }

    private fun loadExercise(id: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getExerciseByIdUseCase(id)
                .onSuccess {
                    _state.update { state ->
                        state.copy(
                            exerciseName = it.canonicalName,
                            weeklyStats = calculateWeeklyStatsUseCase(it.id).getOrNull(),
                            trends = calculateTrendsUseCase(it.id).getOrNull(),
                            isLoading = false,
                            error = null
                        )
                    }
                    refreshAIInsight()
                }
        }
    }

    private fun refreshAIInsight() {
        viewModelScope.launch {
            _state.update { it.copy(isGeneratingAI = true) }
            getExerciseAIInsightUseCase().fold(
                onSuccess = { insight ->
                    _state.update {
                        it.copy(
                            aiInsight = insight,
                            isGeneratingAI = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            error = error.message,
                            isGeneratingAI = false
                        )
                    }
                }
            )
        }
    }
}
