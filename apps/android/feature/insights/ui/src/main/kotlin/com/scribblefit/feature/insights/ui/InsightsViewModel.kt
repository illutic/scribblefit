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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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
        observeData()
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun observeData() {
        viewModelScope.launch {
            combine(
                _state.map { it.startDate }.distinctUntilChanged(),
                _state.map { it.endDate }.distinctUntilChanged(),
                navigator.navState
            ) { start, end, navState ->
                Triple(start, end, navState)
            }.collectLatest { (start, end, navState) ->
                _state.update { it.copy(isLoading = true, bottomBarState = navState.bottomBarState) }
                
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
                            isLoading = false
                        )
                    }
                    if (frequency.totalWorkouts >= 2) {
                        loadAIOverview(start, end)
                    }
                }.catch { error ->
                    _state.update { it.copy(isLoading = false, errorMessage = error.message) }
                }.collect()
            }
        }
    }

    fun onIntent(intent: InsightsIntent) {
        when (intent) {
            InsightsIntent.Refresh -> {
                _state.update { it.copy(isLoading = true) }
                // Re-observation will be triggered if dates change, or we can manually reload here
                val s = _state.value.startDate
                val e = _state.value.endDate
                loadAIOverview(s, e)
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

    private var aiJob: kotlinx.coroutines.Job? = null

    private fun loadAIOverview(startDate: LocalDate, endDate: LocalDate) {
        aiJob?.cancel()
        aiJob = viewModelScope.launch {
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
