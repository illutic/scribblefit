package com.scribblefit.feature.ledger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.ledger.ui.components.EmptyLedgerContent
import com.scribblefit.feature.ledger.ui.components.LedgerHeader
import com.scribblefit.feature.ledger.ui.components.WorkoutItem

@Composable
internal fun LedgerScreen(
    viewModel: LedgerViewModel
) {
    val state by viewModel.state.collectAsState()
    
    LedgerContent(
        state = state,
        onIntent = viewModel::onIntent,
        onCtaClick = viewModel::navigateToCanvas
    )
}

@Composable
private fun LedgerContent(
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit,
    onCtaClick: () -> Unit
) {
    Scaffold(
        topBar = {
            LedgerHeader(
                title = state.ledgerTitle,
                dateRange = state.dateRangeString,
                onDateRangeClick = { /* TODO: Open Date Range Picker */ }
            )
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading && state.workouts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ScribbleFitTheme.colors.primary)
                }
            } else if (state.workouts.isEmpty()) {
                EmptyLedgerContent(
                    title = state.emptyTitle,
                    cta = state.emptyCta,
                    onCtaClick = onCtaClick
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    state.groupedWorkouts.forEach { (date, workouts) ->
                        items(workouts, key = { it.id }) { workout ->
                            WorkoutItem(
                                workout = workout,
                                dateHeader = state.getWorkoutDateHeader(date),
                                onClick = { onIntent(LedgerIntent.WorkoutClicked(workout.id)) }
                            )
                        }
                    }
                }
            }
        }
    }
}
