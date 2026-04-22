package com.scribblefit.feature.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ledger.domain.usecase.GetWorkoutsInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase,
    private val navigator: Navigator
) : ViewModel() {

    private val _state = MutableStateFlow(LedgerState())
    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    private val dateRange = combine(
        _state.map { it.startDate }.distinctUntilChanged(),
        _state.map { it.endDate }.distinctUntilChanged()
    ) { start, end -> start to end }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val workoutsFlow = combine(dateRange, refreshTrigger) { range, _ -> range }
        .flatMapLatest { (start, end) ->
            getWorkoutsInRangeUseCase(start, end)
                .onStart { _state.update { it.copy(isLoading = true) } }
                .onCompletion { _state.update { it.copy(isLoading = false) } }
        }

    val state = combine(_state, workoutsFlow, navigator.navState) { state, workouts, navState ->
        state.copy(
            workouts = workouts.sortedByDescending { it.date },
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

            is LedgerIntent.WorkoutClicked -> {
                navigator.navigateTo(Screen.WorkoutExercises(intent.workoutId))
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
