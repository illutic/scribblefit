package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.ledger.ui.LedgerIntent
import com.scribblefit.feature.ledger.ui.LedgerState

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun LedgerContent(
    modifier: Modifier = Modifier,
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScribbleFitTheme.spacing.large,
            vertical = ScribbleFitTheme.spacing.large
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        items(state.groupedWorkouts, key = { it.date.toString() }) { dailyWorkout ->
            WorkoutItem(
                title = state.getWorkoutDateHeader(dailyWorkout.date),
                exercises = dailyWorkout.exercises,
                onClick = { /* TODO: Navigate to day details */ }
            )
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}