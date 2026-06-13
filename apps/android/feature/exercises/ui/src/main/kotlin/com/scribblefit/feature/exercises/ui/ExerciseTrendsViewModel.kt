package com.scribblefit.feature.exercises.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.exercises.domain.usecase.GetExerciseTrendDataUseCase
import com.scribblefit.feature.exercises.domain.usecase.TrendPeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExerciseTrendsViewModel @Inject constructor(
    private val getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase,
    configRepository: ConfigRepository,
    private val navigator: Navigator
) : ViewModel() {
    private val _state = MutableStateFlow(ExerciseTrendsState())
    private val period = _state.map { it.selectedPeriod }.distinctUntilChanged()
    private val exerciseName = navigator.navState
        .map { (it.backStack.lastOrNull() as? Screen.ExerciseTrends)?.exerciseName }
        .distinctUntilChanged()
    private val dataParams = combine(
        period,
        configRepository.config.map { it.localConfig.weightUnit },
        exerciseName
    ) { period, weightUnit, exerciseName ->
        DataParams(
            period,
            weightUnit,
            exerciseName ?: ""
        )
    }
    private val trends =
        dataParams.flatMapLatest { getExerciseTrendDataUseCase(it.exerciseName, it.period) }

    val state: StateFlow<ExerciseTrendsState> = combine(
        dataParams,
        trends
    ) { (period, _, exerciseName), trendResult ->
        val trends = trendResult.getOrNull()
        ExerciseTrendsState(
            isLoading = false,
            selectedPeriod = period,
            weightUnit = configRepository.config.value.localConfig.weightUnit,
            exerciseName = exerciseName,
            oneRMDataPoints = trends?.oneRM?.dataPoints ?: emptyList(),
            oneRMInsights = trends?.oneRM?.insights,
            volumeDataPoints = trends?.volume?.dataPoints ?: emptyList(),
            volumeInsights = trends?.volume?.insights,
            error = trendResult.exceptionOrNull()?.message
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ExerciseTrendsState(isLoading = true)
    )

    private data class DataParams(
        val period: TrendPeriod,
        val weightUnit: Weight,
        val exerciseName: String
    )

    fun onIntent(intent: ExerciseTrendsIntent) {
        when (intent) {
            is ExerciseTrendsIntent.UpdatePeriod -> {
                _state.update { it.copy(selectedPeriod = intent.period, isLoading = true) }
            }

            ExerciseTrendsIntent.NavigateBack -> navigator.goBack()
        }
    }
}
