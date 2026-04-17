package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.ledger.ui.LedgerIntent
import com.scribblefit.feature.ledger.ui.LedgerState

@Composable
internal fun LedgerContent(
    modifier: Modifier = Modifier,
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit,
) {
    LazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScribbleFitTheme.spacing.large,
            vertical = ScribbleFitTheme.spacing.small
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
        columns = GridCells.Adaptive(minSize = 300.dp)
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
        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}