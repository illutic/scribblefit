package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExerciseDetailsRoute(
    exerciseName: String,
    viewModel: ExerciseDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(exerciseName) {
        viewModel.onIntent(ExerciseDetailsIntent.LoadDetails(exerciseName))
    }
    ExerciseDetailsScreen(viewModel = viewModel)
}
