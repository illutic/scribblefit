package com.scribblefit.feature.canvas.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.component.ScribbleFitCard
import com.scribblefit.core.designsystem.component.ScribbleFitPill
import com.scribblefit.core.designsystem.component.ScribbleFitTextField
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitShapes
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import com.scribblefit.feature.canvas.domain.model.FeedItem
import com.scribblefit.feature.canvas.domain.model.ScribbleStatus
import com.scribblefit.feature.canvas.domain.usecase.QuickActionType

private val ErrorBackground = Color(0xFFFEE2E2)
private val ErrorText = Color(0xFF991B1B)
private const val PendingAlpha = 0.5f
private const val PromptMaxWidthFraction = 0.85f
private const val ConfirmationMaxWidthFraction = 0.9f
private const val PulseAnimationDurationMs = 500

@Composable
fun CanvasHeader(
    userName: String, 
    greeting: String,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ScribbleFitSpacing.Medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$greeting, ${userName.uppercase()}",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun ContextualInsightCard(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 32.sp
        )
    )
}

@Composable
fun FeedItemRow(
    item: FeedItem,
    onRetry: (String) -> Unit = {},
    onConfirmClick: (FeedItem.Confirmation) -> Unit = {}
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
            is FeedItem.Confirmation -> ConfirmationCard(item, onConfirmClick)
            is FeedItem.Insight -> InsightBubble(item)
        }
    }
}

@Composable
private fun PromptBubble(item: FeedItem.Prompt) {
    Column(modifier = Modifier.fillMaxWidth(PromptMaxWidthFraction)) {
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
        ScribbleStatus.FAILED -> ErrorBackground
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val alpha = when (item.status) {
        ScribbleStatus.PENDING, ScribbleStatus.PROCESSING -> PendingAlpha
        else -> 1f
    }

    Column(horizontalAlignment = Alignment.End, modifier = Modifier.alpha(alpha)) {
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
                        color = if (item.status == ScribbleStatus.FAILED) ErrorText else MaterialTheme.colorScheme.onSurface
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
                color = ErrorText,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .clickable { onRetry(item.id) }
            )
        }
    }
}

@Composable
private fun ConfirmationCard(
    item: FeedItem.Confirmation,
    onConfirmClick: (FeedItem.Confirmation) -> Unit
) {
    ScribbleFitCard(
        modifier = Modifier.fillMaxWidth(ConfirmationMaxWidthFraction),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        item.workout.exercises.forEachIndexed { index, exercise ->
            if (index > 0) Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = exercise.canonicalName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = exercise.sets.joinToString("  ") { set ->
                    if (set.weight > 0) "${set.weight.toInt()}×${set.reps}" else "×${set.reps}"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ScribbleFitPill(
                text = "Confirm",
                onClick = { onConfirmClick(item) },
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
private fun InsightBubble(item: FeedItem.Insight) {
    Surface(
        shape = ScribbleFitShapes.Medium,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
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
fun QuickActionPills(
    actions: List<QuickActionType>,
    onActionClick: (QuickActionType) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(actions) { action ->
            val label = when (action) {
                QuickActionType.REPEAT_LAST -> "Repeat last workout"
                QuickActionType.REST_DAY -> "Rest Day"
                QuickActionType.RUN_5K -> "Log 5k Run"
            }
            ScribbleFitPill(
                text = label,
                onClick = { onActionClick(action) }
            )
        }
    }
}

@Composable
fun ScribbleInputPill(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onMicClick: () -> Unit,
    isSyncing: Boolean,
    isRecording: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) {
            1.2f
        } else {
            1f
        },
        animationSpec = infiniteRepeatable(
            animation = tween(PulseAnimationDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    ScribbleFitTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = if (isRecording) "Listening..." else "Message ScribbleFit...",
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Box(contentAlignment = Alignment.Center) {
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
                        onClick = onMicClick,
                        modifier = Modifier
                            .size(32.dp)
                            .scale(scale)
                    ) {
                        Text(
                            text = if (isRecording) "⏹️" else "🎙️", 
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    )
}
