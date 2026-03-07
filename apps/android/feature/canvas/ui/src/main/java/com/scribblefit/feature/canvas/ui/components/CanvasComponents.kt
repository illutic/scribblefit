package com.scribblefit.feature.canvas.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.component.ScribbleFitCard
import com.scribblefit.core.designsystem.component.ScribbleFitPill
import com.scribblefit.core.designsystem.component.ScribbleFitTextField
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitShapes
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import com.scribblefit.feature.canvas.R
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.model.ScribbleStatus

@Composable
fun CanvasHeader(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "EVENING, ${userName.uppercase()}",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun FeedItemRow(
    item: FeedItem,
    onRetry: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ScribbleFitSpacing.Small),
        contentAlignment = when (item) {
            is FeedItem.Scribble -> Alignment.CenterEnd
            else -> Alignment.CenterStart
        }
    ) {
        when (item) {
            is FeedItem.Prompt -> PromptBubble(item)
            is FeedItem.Scribble -> ScribbleBubble(item, onRetry)
            is FeedItem.Confirmation -> ConfirmationCard(item)
            is FeedItem.Insight -> InsightBubble(item)
        }
    }
}

@Composable
private fun PromptBubble(item: FeedItem.Prompt) {
    Column(modifier = Modifier.fillMaxWidth(0.85f)) {
        Text(
            text = "${item.text} ${item.emoji}",
            style = MaterialTheme.typography.headlineSmall.copy(
                lineHeight = 32.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}

@Composable
private fun ScribbleBubble(
    item: FeedItem.Scribble,
    onRetry: (String) -> Unit
) {
    val backgroundColor = when (item.status) {
        ScribbleStatus.FAILED -> Color(0xFFFEE2E2)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Column(horizontalAlignment = Alignment.End) {
        Surface(
            shape = ScribbleFitShapes.Large,
            color = backgroundColor,
            modifier = Modifier.animateContentSize()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.rawText,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (item.status == ScribbleStatus.FAILED) Color(0xFF991B1B) else MaterialTheme.colorScheme.onSurface
                    )
                )

                if (item.status == ScribbleStatus.PENDING || item.status == ScribbleStatus.PROCESSING) {
                    Spacer(modifier = Modifier.width(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(12.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (item.status == ScribbleStatus.FAILED) {
            Text(
                text = "Failed to parse. Tap to retry.",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFF991B1B),
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onRetry(item.id) }
            )
        }
    }
}

@Composable
private fun ConfirmationCard(item: FeedItem.Confirmation) {
    ScribbleFitCard(
        modifier = Modifier.fillMaxWidth(0.9f),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = item.workout.exercises.firstOrNull()?.canonicalName ?: "Workout Logged",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${item.workout.exercises.sumOf { it.sets.size }} sets completed.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ScribbleFitPill(
                text = "Confirm",
                onClick = { },
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
            ScribbleFitPill(text = "Edit", onClick = { })
        }
    }
}

@Composable
private fun InsightBubble(item: FeedItem.Insight) {
    Surface(
        shape = ScribbleFitShapes.Medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
        border = AssistChipDefaults.assistChipBorder(enabled = true)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = item.emoji, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun QuickActionPills(pills: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(pills) { pill ->
            ScribbleFitPill(
                text = pill,
                onClick = { }
            )
        }
    }
}

@Composable
fun ScribbleInputPill(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSyncing: Boolean
) {
    ScribbleFitTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = "Message ScribbleFit...",
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Box {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = onSubmit,
                        enabled = !isSyncing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Submit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { /* Mic action */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.mic_24dp),
                            contentDescription = "Mic"
                        )
                    }
                }
            }
        }
    )
}
