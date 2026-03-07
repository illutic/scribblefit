package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.feature.canvas.ui.components.*
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing

@Composable
fun CanvasScreen(
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val text by viewModel.scribbleText.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ScribbleFitSpacing.ScreenPadding)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start
        ) {
            CanvasHeader(userName = "George")
            
            Spacer(modifier = Modifier.height(ScribbleFitSpacing.XL))

            ContextualInsightCard(
                text = "You hit chest on Thursday.\nReady for a Pull day? 💪"
            )

            Spacer(modifier = Modifier.height(ScribbleFitSpacing.XL))

            QuickActionPills(
                pills = listOf("Repeat last Pull Day", "Log 5k Southsea run", "Rest Day")
            )

            // Feed area
            Spacer(modifier = Modifier.weight(1f))
        }

        // Fixed Input area at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = ScribbleFitSpacing.Medium)
        ) {
            ScribbleInputPill(
                text = text,
                onTextChange = viewModel::onTextChange,
                onSubmit = viewModel::submitScribble,
                isSyncing = isSyncing
            )
        }
    }
}
