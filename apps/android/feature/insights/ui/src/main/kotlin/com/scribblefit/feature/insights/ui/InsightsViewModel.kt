package com.scribblefit.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.insights.domain.model.AIOverview
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.insights.domain.usecase.GetFrequencyInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetMuscleDistributionInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetVolumeInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
class InsightsViewModel @Inject constructor(
    private val getVolumeInsightsUseCase: GetVolumeInsightsUseCase,
    private val getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase,
    private val getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase,
    private val getAIOverviewUseCase: GetAIOverviewUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())
    
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    val state: StateFlow<InsightsState> = combine(
        _state.map { it.startDate }.distinctUntilChanged(),
        _state.map { it.endDate }.distinctUntilChanged(),
        navigator.navState
    ) { start, end, navState ->
        Triple(start, end, navState)
    }.flatMapLatest { (start, end, navState) ->
        combine(
            getVolumeInsightsUseCase(start, end),
            getFrequencyInsightsUseCase(start, end),
            getMuscleDistributionInsightsUseCase(start, end)
        ) { volume, frequency, distribution ->
            _state.update {
                it.copy(
                    volumePoints = volume,
                    frequency = frequency,
                    distribution = distribution,
                    bottomBarState = navState.bottomBarState,
                    isLoading = false
                )
            }
            if (frequency.totalWorkouts >= 2) {
                loadAIOverview(start, end)
            }
        }
    }.catch { error ->
        _state.update { it.copy(isLoading = false, errorMessage = error.message) }
    }.map { 
        _state.value 
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = _state.value
    )

    fun onIntent(intent: InsightsIntent) {
        when (intent) {
            InsightsIntent.Refresh -> {
                _state.update { it.copy(isLoading = true) }
            }
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
    }

    private fun loadAIOverview(startDate: LocalDate, endDate: LocalDate) {
        if (_state.value.isGeneratingAI) return

        viewModelScope.launch {
            _state.update { it.copy(isGeneratingAI = true) }

            getAIOverviewUseCase(startDate, endDate).fold(
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
