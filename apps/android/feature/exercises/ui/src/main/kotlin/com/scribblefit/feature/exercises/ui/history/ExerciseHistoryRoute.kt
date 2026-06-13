package com.scribblefit.feature.exercises.ui.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExerciseHistoryRoute(
    exerciseName: String,
    viewModel: ExerciseHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(exerciseName) {
        viewModel.onIntent(ExerciseHistoryIntent.LoadHistory(exerciseName))
    }

    ExerciseHistoryScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}
