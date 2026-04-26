package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExerciseDetailsRoute(
    exerciseId: Long,
    viewModel: ExerciseDetailsViewModel = viewModel()
) {
    LaunchedEffect(exerciseId) {
        viewModel.onIntent(ExerciseDetailsIntent.LoadDetails(exerciseId))
    }
    ExerciseDetailsScreen(viewModel = viewModel)
}
