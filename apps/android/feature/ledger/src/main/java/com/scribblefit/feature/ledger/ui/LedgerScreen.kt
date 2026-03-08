package com.scribblefit.feature.ledger.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.core.designsystem.ScribbleFitCard
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val DateFormatter = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

@Composable
fun LedgerScreen(
    modifier: Modifier = Modifier,
    viewModel: LedgerViewModel = hiltViewModel()
) {
    val history by viewModel.workoutHistory.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize().padding(ScribbleFitSpacing.screenPadding)) {
        Text(
            text = "Workouts",
            color = ScribbleFitColors.RichBlack,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = ScribbleFitSpacing.Medium)
        )
        LazyColumn(verticalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small)) {
            items(history) { workout ->
                WorkoutHistoryCard(workout = workout)
            }
        }
    }
}

@Composable
private fun WorkoutHistoryCard(workout: WorkoutHistory) {
    ScribbleFitCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small)) {
            Text(
                text = DateFormatter.format(Date(workout.date)),
                color = ScribbleFitColors.RichBlack,
                fontSize = 16.sp
            )
            Text(
                text = "${workout.exercises.size} exercises · ${workout.totalVolume.toInt()} lbs",
                color = ScribbleFitColors.MidGray,
                fontSize = 14.sp
            )
        }
    }
}
