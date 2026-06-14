package com.scribblefit.feature.canvas.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate

@Composable
fun CanvasRoute(
    dateEpochDays: Long? = null,
    viewModel: CanvasViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(dateEpochDays) {
        if (dateEpochDays != null) {
            val localDate = LocalDate.ofEpochDay(dateEpochDays).atStartOfDay()
            viewModel.onIntent(CanvasIntent.OnDateSelected(localDate))
        }
    }

    CanvasScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}
