package com.scribblefit.feature.ledger.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val FLOW_TIMEOUT_MS = 5_000L

@HiltViewModel
class LedgerViewModel @Inject constructor(
    private val ledgerRepository: LedgerRepository
) : ViewModel() {

    val workoutHistory: StateFlow<List<WorkoutHistory>> = ledgerRepository
        .getWorkoutHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS), emptyList())
}
