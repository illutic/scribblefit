package com.scribblefit.feature.exercises.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExerciseTrendsRoute(
    viewModel: ExerciseTrendsViewModel = viewModel()
) {
    ExerciseTrendsScreen(viewModel = viewModel)
}
