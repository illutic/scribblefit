package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ExerciseDetailsRoute(
    exerciseId: Long,
    viewModel: ExerciseDetailsViewModel = hiltViewModel()
) {
    LaunchedEffect(exerciseId) {
        viewModel.onIntent(ExerciseDetailsIntent.LoadDetails(exerciseId))
    }
    ExerciseDetailsScreen(viewModel = viewModel)
}
