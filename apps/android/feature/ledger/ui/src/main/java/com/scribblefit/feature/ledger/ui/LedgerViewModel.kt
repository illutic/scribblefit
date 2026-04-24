package com.scribblefit.feature.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.feature.exercises.domain.usecase.GetExercisesInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val getExercisesInRangeUseCase: GetExercisesInRangeUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(LedgerState())
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val dateRange = combine(
        _state.map { it.startDate }.distinctUntilChanged(),
        _state.map { it.endDate }.distinctUntilChanged()
    ) { start, end -> CurrentDate(start) to CurrentDate(end) }

    private val exercisesFlow = combine(
        dateRange,
        refreshTrigger
    ) { range, _ ->
        _state.update { it.copy(isLoading = true) }

        getExercisesInRangeUseCase(range.first, range.second).getOrNull() ?: emptyList()
    }

    val state = combine(_state, exercisesFlow, navigator.navState) { state, exercises, navState ->
        state.copy(
            exercises = exercises.sortedByDescending { it.createdAt },
            isLoading = false,
            bottomBarState = navState.bottomBarState
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LedgerState()
    )

    fun onIntent(intent: LedgerIntent) {
        when (intent) {
            is LedgerIntent.DateRangeChanged -> {
                val startDate = intent.startDate?.toLocalDate() ?: _state.value.startDate
                val endDate = intent.endDate?.toLocalDate() ?: _state.value.endDate
                _state.update { it.copy(startDate = startDate, endDate = endDate) }
            }

            LedgerIntent.HideDatePicker -> {
                _state.update { it.copy(showDatePicker = false) }
            }

            LedgerIntent.ShowDatePicker -> {
                _state.update {
                    it.copy(showDatePicker = true)
                }
            }

            LedgerIntent.Refresh -> {
                fetchWorkouts()
            }

            is LedgerIntent.NavigateToScreen -> {
                navigator.navigateTo(intent.screen)
            }
        }
    }

    private fun fetchWorkouts() {
        viewModelScope.launch {
            refreshTrigger.emit(Unit)
        }
    }
}
