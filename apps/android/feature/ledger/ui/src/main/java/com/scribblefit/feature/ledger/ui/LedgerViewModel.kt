package com.scribblefit.feature.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.core.navigation.Navigator
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ledger.domain.usecase.GetWorkoutsInRangeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
    
    private val dateRange = combine(
        _state.map { it.startDate }.distinctUntilChanged(),
        _state.map { it.endDate }.distinctUntilChanged()
    ) { start, end -> start to end }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val workoutsFlow = dateRange.flatMapLatest { (start, end) ->
        getWorkoutsInRangeUseCase(start, end)
            .onStart { _state.update { it.copy(isLoading = true) } }
            .onCompletion { _state.update { it.copy(isLoading = false) } }
    }

    val state = combine(_state, workoutsFlow) { state, workouts ->
        state.copy(
            workouts = workouts.sortedByDescending { it.date },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = LedgerState()
    )

    fun onIntent(intent: LedgerIntent) {
        when (intent) {
            is LedgerIntent.DateRangeChanged -> {
                _state.update { it.copy(startDate = intent.startDate, endDate = intent.endDate) }
            }
            is LedgerIntent.WorkoutClicked -> {
                // TODO: Navigate to workout details once implemented
                // navigator.navigateTo(Screen.WorkoutDetails(intent.workoutId))
            }
        }
    }

    fun navigateToCanvas() {
        navigator.navigateTo(Screen.Canvas)
    }
}

private fun <T, R> kotlinx.coroutines.flow.Flow<T>.map(transform: suspend (T) -> R): kotlinx.coroutines.flow.Flow<R> =
    kotlinx.coroutines.flow.map { transform(it) }
