package com.scribblefit.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.insights.domain.usecase.GetFrequencyInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetMuscleDistributionInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetVolumeInsightsUseCase
import com.scribblefit.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getVolumeInsightsUseCase: GetVolumeInsightsUseCase,
    private val getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase,
    private val getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase,
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
                }
        }
    }
}
