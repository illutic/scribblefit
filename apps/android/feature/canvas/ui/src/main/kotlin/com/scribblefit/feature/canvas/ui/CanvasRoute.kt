package com.scribblefit.feature.canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CanvasRoute(
    viewModel: CanvasViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CanvasScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
