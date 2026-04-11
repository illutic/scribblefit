package com.scribblefit.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.insights.domain.model.AIOverview
import com.scribblefit.feature.insights.domain.usecase.*
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneOffset
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
            is InsightsIntent.SelectPeriod -> selectPeriod(intent.period)
        }
    }

    private fun selectPeriod(period: InsightsPeriod) {
        val now = LocalDate.now()
        val startDate = when (period) {
            InsightsPeriod.DAILY -> now.minusDays(1)
            InsightsPeriod.WEEKLY -> now.minusWeeks(1)
            InsightsPeriod.MONTHLY -> now.minusMonths(1)
        }
        _state.update {
            it.copy(
                selectedPeriod = period,
                startDate = startDate,
                endDate = now
            )
        }
        loadInsights()
    }

    private fun loadInsights() {
        _state.update { it.copy(isLoading = true) }
        val startDate = _state.value.startDate
        val endDate = _state.value.endDate

        val volumeFlow = getVolumeInsightsUseCase(startDate, endDate)
        val frequencyFlow = getFrequencyInsightsUseCase(startDate, endDate)
        val distributionFlow = getMuscleDistributionInsightsUseCase(startDate, endDate)

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
                onSuccess = { insights ->
                    _state.update {
                        it.copy(
                            isGeneratingAI = false,
                            aiOverview = AIOverview(insights = insights)
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
