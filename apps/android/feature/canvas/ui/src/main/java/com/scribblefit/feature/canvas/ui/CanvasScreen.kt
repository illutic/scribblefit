package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import com.scribblefit.feature.canvas.ui.components.CanvasHeader
import com.scribblefit.feature.canvas.ui.components.ContextualInsightCard
import com.scribblefit.feature.canvas.ui.components.FeedItemRow
import com.scribblefit.feature.canvas.ui.components.QuickActionPills
import com.scribblefit.feature.canvas.ui.components.ScribbleInputPill

@Composable
fun CanvasScreen(
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new items arrive
    LaunchedEffect(uiState.feedItems.size) {
        if (uiState.feedItems.isNotEmpty()) {
            listState.animateScrollToItem(uiState.feedItems.size - 1)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScribbleFitSpacing.ScreenPadding)
                    .padding(bottom = ScribbleFitSpacing.Medium)
                    .imePadding() // Lift with keyboard
            ) {
                ScribbleInputPill(
                    text = uiState.scribbleText,
                    onTextChange = viewModel::onTextChange,
                    onSubmit = viewModel::submitScribble,
                    onMicClick = viewModel::onMicClick,
                    isSyncing = uiState.isSyncing,
                    isRecording = uiState.isRecording
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = ScribbleFitSpacing.ScreenPadding)
        ) {
            CanvasHeader(
                userName = uiState.userName,
                greeting = uiState.greeting,
                onMenuClick = viewModel::onMenuClick
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = ScribbleFitSpacing.Medium)
            ) {
                // If feed is empty and we have a suggestion, show the suggestion first
                if (uiState.feedItems.isEmpty() && uiState.homeSuggestion != null) {
                    item {
                        ContextualInsightCard(
                            text = uiState.homeSuggestion?.fullText ?: ""
                        )
                        Spacer(modifier = Modifier.height(ScribbleFitSpacing.Large))
                    }
                }

                items(
                    items = uiState.feedItems,
                    key = { it.id }
                ) { item ->
                    FeedItemRow(
                        item = item,
                        onRetry = viewModel::onRetryScribble,
                        onConfirmClick = viewModel::onConfirmClick
                    )
                }

                // Show Quick Actions if feed is empty or very short
                if (uiState.feedItems.size <= 1) {
                    item {
                        Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                        QuickActionPills(
                            actions = uiState.quickActions,
                            onActionClick = viewModel::onQuickActionClick
                        )
                    }
                }
            }
        }
    }
}
