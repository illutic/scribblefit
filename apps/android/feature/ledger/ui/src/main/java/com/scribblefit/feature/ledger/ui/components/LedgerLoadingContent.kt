package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.ledger.ui.LedgerIntent
import com.scribblefit.feature.ledger.ui.LedgerState

@Composable
internal fun LedgerContent(
    modifier: Modifier = Modifier,
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        when {
            state.isLoading && state.workouts.isEmpty() -> {
                items(5) {
                    LedgerSkeletonItem()
                }
            }

            state.workouts.isEmpty() -> {
                item {
                    EmptyLedgerContent(
                        title = state.emptyTitle,
                        cta = state.emptyCta,
                        onCtaClick = { onIntent(LedgerIntent.NavigateToScreen(Screen.Canvas)) }
                    )
                }
            }

            else -> {
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

        item {
            Spacer(
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

private fun asdasd {

    stickyHeader {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DateRangePicker(
                dateRange = state.dateRangeString,
                onDateRangeClick = { onIntent(LedgerIntent.ShowDatePicker) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}