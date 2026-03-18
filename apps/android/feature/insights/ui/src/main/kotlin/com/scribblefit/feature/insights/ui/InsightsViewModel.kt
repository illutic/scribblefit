package com.scribblefit.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.insights.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getVolumeInsightsUseCase: GetVolumeInsightsUseCase,
    private val getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase,
    private val getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase,
    private val getAIOverviewUseCase: GetAIOverviewUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())
    val state: StateFlow<InsightsState> = _state.asStateFlow()

    init {
        loadInsights()
    }

    fun onIntent(intent: InsightsIntent) {
        when (intent) {
            InsightsIntent.Refresh -> loadInsights()
            is InsightsIntent.NavigateToScreen -> navigator.navigateTo(intent.screen)
        }
    }

    private fun loadInsights() {
        _state.update { it.copy(isLoading = true) }

        val startDate = LocalDate.now().minusMonths(1)
        val endDate = LocalDate.now()

        val volumeFlow = getVolumeInsightsUseCase(startDate, endDate)
        val frequencyFlow = getFrequencyInsightsUseCase()
        val distributionFlow = getMuscleDistributionInsightsUseCase()

        viewModelScope.launch {
            combine(volumeFlow, frequencyFlow, distributionFlow) { volume, frequency, distribution ->
                Triple(volume, frequency, distribution)
            }
                .catch { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message
                        )
                    }
                }
                .collect { (volume, frequency, distribution) ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            volumePoints = volume,
                            frequency = frequency,
                            distribution = distribution
                        )
                    }
                    if (frequency.totalWorkouts >= 2) {
                        loadAIOverview()
                    }
                }
        }
    }

    private fun loadAIOverview() {
        if (_state.value.isGeneratingAI) return

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingAI = true) }
            getAIOverviewUseCase().fold(
                onSuccess = { overview ->
                    _state.update {
                        it.copy(
                            isGeneratingAI = false,
                            aiOverview = overview
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isGeneratingAI = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }
}
