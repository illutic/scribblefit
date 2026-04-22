package com.scribblefit.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseAIInsightUseCase
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseDetailsViewModel @Inject constructor(
    private val getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
    private val getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
    private val configRepository: com.scribblefit.core.config.domain.ConfigRepository,
    private val navigator: Navigator,
) : ViewModel() {

    private val _state = MutableStateFlow(ExerciseDetailsState())
    val state: StateFlow<ExerciseDetailsState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            configRepository.config.collect { config ->
                _state.update { it.copy(weightUnit = config.weightUnit) }
            }
        }
    }

    fun onIntent(intent: ExerciseDetailsIntent) {
        when (intent) {
            is ExerciseDetailsIntent.LoadDetails -> loadDetails(intent.exerciseName)
            ExerciseDetailsIntent.RefreshAIInsight -> refreshAIInsight()
            ExerciseDetailsIntent.NavigateBack -> navigator.goBack()
        }
    }

    private fun loadDetails(name: String) {
        viewModelScope.launch {
            _state.update { it.copy(exerciseName = name, isLoading = true) }
            getExerciseDetailsUseCase(name).collectLatest { details ->
                _state.update { 
                    it.copy(
                        details = details,
                        isLoading = false
                    )
                }
                // Auto-trigger AI insight if we have history
                if (details.history.isNotEmpty() && _state.value.aiInsight == null) {
                    refreshAIInsight()
                }
            }
        }
    }

    private fun refreshAIInsight() {
        val history = _state.value.details?.history ?: return
        if (history.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingAI = true) }
            getExerciseAIInsightUseCase(history).fold(
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
