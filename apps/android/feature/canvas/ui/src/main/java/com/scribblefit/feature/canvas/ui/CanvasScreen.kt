package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import com.scribblefit.feature.canvas.ui.components.*

@Composable
fun CanvasScreen(
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val text by viewModel.scribbleText.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val feedItems by viewModel.feedItems.collectAsState()
    
    val listState = rememberLazyListState()

    // Auto-scroll to bottom when new items arrive
    LaunchedEffect(feedItems.size) {
        if (feedItems.isNotEmpty()) {
            listState.animateScrollToItem(feedItems.size - 1)
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
                    text = text,
                    onTextChange = viewModel::onTextChange,
                    onSubmit = viewModel::submitScribble,
                    isSyncing = isSyncing
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
            CanvasHeader(userName = "George")
            
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = ScribbleFitSpacing.Medium)
            ) {
                items(
                    items = feedItems,
                    key = { it.id }
                ) { item ->
                    FeedItemRow(
                        item = item,
                        onRetry = { /* viewModel.retry(it) */ }
                    )
                }
                
                // Add Quick Actions at the end of the feed if it's empty or just a suggestion
                if (feedItems.size <= 1) {
                    item {
                        Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))
                        QuickActionPills(
                            pills = listOf("Repeat last Pull Day", "Log 5k Run", "Rest Day")
                        )
                    }
                }
            }
        }
    }
}
