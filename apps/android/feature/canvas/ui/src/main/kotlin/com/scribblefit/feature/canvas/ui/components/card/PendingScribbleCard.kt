package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun PendingScribbleCard(scribble: ScribbleUiModel) {
    val infiniteTransition = rememberInfiniteTransition(label = "parsing")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    ScribbleCardContainer {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            ScribbleRawText(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Rounded.Sync,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.primary,
                modifier = Modifier
                    .size(ScribbleFitTheme.spacing.large)
                    .rotate(rotation)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(ScribbleFitTheme.colors.surfaceContainerHigh, CircleShape)
        ) {
            val progressWidth by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "progress"
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressWidth)
                    .fillMaxHeight()
                    .background(ScribbleFitTheme.colors.primary, CircleShape)
            )
        }

        scribble.statusText?.let {
            Text(
                text = it.uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.4f),
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
