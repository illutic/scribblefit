package com.scribblefit.feature.insights.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.insights.domain.usecase.GetAIOverviewUseCase
import com.scribblefit.feature.insights.domain.usecase.GetFrequencyInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetMuscleDistributionInsightsUseCase
import com.scribblefit.feature.insights.domain.usecase.GetVolumeInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
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
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class InsightsViewModel @Inject constructor(
    private val getVolumeInsightsUseCase: GetVolumeInsightsUseCase,
    private val getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase,
    private val getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase,
    private val getAIOverviewUseCase: GetAIOverviewUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(InsightsState())

    private val stateWithBottomBarState = combine(
        _state,
        navigator.navState
    ) { state, navState ->
        state.copy(bottomBarState = navState.bottomBarState)
    }

    private val dateRangeFlow = combine(
        _state.map { CurrentDate(it.startDate) }.distinctUntilChanged(),
        _state.map { CurrentDate(it.endDate) }.distinctUntilChanged()
    ) { start, end -> start to end }

    private val frequencyInsights = dateRangeFlow.flatMapLatest { (start, end) ->
        getFrequencyInsightsUseCase(start, end)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    private val volumeInsights = dateRangeFlow.flatMapLatest { (start, end) ->
        getVolumeInsightsUseCase(start, end)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    private val muscleDistributionInsights = dateRangeFlow.flatMapLatest { (start, end) ->
        getMuscleDistributionInsightsUseCase(start, end)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    private val aiOverviewInsights = dateRangeFlow.flatMapLatest { (start, end) ->
        getAIOverviewUseCase(start, end)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
    }

    val state: StateFlow<InsightsState> = combine(
        stateWithBottomBarState,
        frequencyInsights,
        volumeInsights,
        muscleDistributionInsights,
        aiOverviewInsights
    ) { state, frequencyResult, volumeResult, muscleDistributionResult, insights ->
        state.copy(
            insights = insights.getOrNull(),
            frequency = frequencyResult.getOrNull(),
            volumePoints = volumeResult.getOrDefault(emptyList()),
            distribution = muscleDistributionResult.getOrDefault(emptyList()),
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InsightsState())

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
                endDate = now,
                isLoading = true
            )
        }
    }
}
