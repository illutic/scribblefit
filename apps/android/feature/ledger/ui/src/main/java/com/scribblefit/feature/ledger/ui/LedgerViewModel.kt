package com.scribblefit.feature.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.config.domain.ConfigRepository
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.scribble.domain.usecase.GetScribblesInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.time.Instant
import java.time.ZoneOffset

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val getScribblesInRangeUseCase: GetScribblesInRangeUseCase,
    private val configRepository: ConfigRepository,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(LedgerState())
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val dateRange = combine(
        _state.map { it.startDate }.distinctUntilChanged(),
        _state.map { it.endDate }.distinctUntilChanged()
    ) { start, end -> CurrentDate(start) to CurrentDate(end) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val scribblesFlow = combine(
        dateRange,
        refreshTrigger
    ) { range, _ ->
        _state.update { it.copy(isLoading = true) }
        range
    }.flatMapLatest { range ->
        getScribblesInRangeUseCase(range.first, range.second)
    }

    private val preferredWeight = configRepository.config
        .map { it.localConfig.weightUnit }
        .distinctUntilChanged()

    val state = combine(
        _state,
        scribblesFlow,
        navigator.navState,
        preferredWeight
    ) { state, scribbles, navState, weightUnit ->
        state.copy(
            scribbles = scribbles.sortedByDescending { it.createdAt },
            isLoading = false,
            bottomBarState = navState.bottomBarState,
            weightUnit = weightUnit
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LedgerState()
    )

    fun onIntent(intent: LedgerIntent) {
        when (intent) {
            is LedgerIntent.DateRangeChanged -> {
                val startDate = intent.startDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC)
                        .toLocalDate().atStartOfDay()
                } ?: _state.value.startDate
                val endDate = intent.endDate?.let {
                    Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC)
                        .toLocalDate().atTime(23, 59, 59, 999999999)
                } ?: _state.value.endDate
                _state.update { it.copy(startDate = startDate, endDate = endDate) }
            }

            LedgerIntent.HideDatePicker -> {
                _state.update { it.copy(showDatePicker = false) }
            }

            LedgerIntent.ShowDatePicker -> {
                _state.update { it.copy(showDatePicker = true) }
            }

            LedgerIntent.Refresh -> {
                fetchScribbles()
            }

            is LedgerIntent.NavigateToScreen -> {
                navigator.navigateTo(intent.screen)
            }

            is LedgerIntent.NavigateToExerciseDetails -> {
                navigator.navigateTo(Screen.ExerciseDetails(intent.exerciseId))
            }

            is LedgerIntent.ScribbleTapped -> {
                val scribble = state.value.scribbles.find { it.id == intent.scribbleId }
                _state.update { it.copy(selectedScribble = scribble) }
            }

            LedgerIntent.DismissScribbleDetails -> {
                _state.update { it.copy(selectedScribble = null) }
            }
        }
    }

    private fun fetchScribbles() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}
