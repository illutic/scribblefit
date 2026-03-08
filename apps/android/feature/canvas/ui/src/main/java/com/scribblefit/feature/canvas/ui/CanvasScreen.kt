package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitPill
import com.scribblefit.core.designsystem.ScribbleFitSpacing
import com.scribblefit.core.designsystem.ScribbleFitTextField
import com.scribblefit.feature.canvas.domain.model.FeedItem

@Composable
fun CanvasScreen(
    modifier: Modifier = Modifier,
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScribbleFitColors.Background)
            .padding(ScribbleFitSpacing.screenPadding)
    ) {
        Text(
            text = "${uiState.greeting}, ${uiState.userName}",
            color = ScribbleFitColors.RichBlack,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = ScribbleFitSpacing.Medium)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
            contentPadding = PaddingValues(bottom = ScribbleFitSpacing.Medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(uiState.quickActions) { action ->
                ScribbleFitPill(
                    text = action.name.lowercase().replace('_', ' '),
                )
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small)
        ) {
            items(uiState.feedItems, key = { it.feedId() }) { item ->
                FeedItemRow(item = item, onConfirm = viewModel::onConfirmClick)
            }
        }

        ScribbleFitTextField(
            value = uiState.scribbleText,
            onValueChange = viewModel::onTextChange,
            placeholder = "Log workout… e.g. Bench 135x5x3",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ScribbleFitSpacing.Medium)
        )
    }
}

@Composable
private fun FeedItemRow(
    item: FeedItem,
    onConfirm: (FeedItem.Confirmation) -> Unit
) {
    when (item) {
        is FeedItem.Scribble -> ScribbleRow(item = item)
        is FeedItem.Confirmation -> ConfirmationRow(item = item, onConfirm = onConfirm)
        is FeedItem.Prompt -> PromptRow(item = item)
        is FeedItem.Insight -> InsightRow(item = item)
    }
}

@Composable
private fun ScribbleRow(item: FeedItem.Scribble) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = item.rawText,
            color = ScribbleFitColors.RichBlack,
            fontSize = 16.sp,
            modifier = Modifier
                .background(ScribbleFitColors.SoftGray)
                .padding(ScribbleFitSpacing.Medium)
        )
    }
}

@Composable
private fun ConfirmationRow(item: FeedItem.Confirmation, onConfirm: (FeedItem.Confirmation) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Parsed: ${item.workout.exercises.size} exercises",
            color = ScribbleFitColors.RichBlack,
            fontSize = 16.sp
        )
        ScribbleFitPill(text = "Confirm", modifier = Modifier.align(Alignment.End))
    }
}

@Composable
private fun PromptRow(item: FeedItem.Prompt) {
    Text(
        text = "${item.emoji} ${item.text}",
        color = ScribbleFitColors.MidGray,
        fontSize = 14.sp
    )
}

@Composable
private fun InsightRow(item: FeedItem.Insight) {
    Text(
        text = "${item.emoji} ${item.text}",
        color = ScribbleFitColors.MidGray,
        fontSize = 14.sp
    )
}

private fun FeedItem.feedId(): String = when (this) {
    is FeedItem.Prompt -> id
    is FeedItem.Scribble -> id
    is FeedItem.Confirmation -> id
    is FeedItem.Insight -> id
}
