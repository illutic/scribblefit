package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun WorkoutExercisesRoute(
    workoutId: Long,
    viewModel: WorkoutExercisesViewModel = hiltViewModel()
) {
    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }
    val state by viewModel.state.collectAsState()
    WorkoutExercisesScreen(state = state, onIntent = viewModel::onIntent)
}
