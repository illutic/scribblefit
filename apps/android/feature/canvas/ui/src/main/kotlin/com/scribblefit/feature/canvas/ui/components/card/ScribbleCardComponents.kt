package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.ExerciseUiModel

@Composable
internal fun ScribbleCardContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    border: BorderStroke? = null,
    alpha: Float = 1f,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        border = border,
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.large),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
            content = content
        )
    }
}

@Composable
internal fun ScribbleRawText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = ScribbleFitTheme.typography.bodyMedium,
    color: Color = ScribbleFitTheme.colors.midGray
) {
    Text(
        text = "\"$text\"",
        style = style,
        fontStyle = FontStyle.Italic,
        color = color,
        modifier = modifier
    )
}

@Composable
internal fun ExerciseSummary(
    summary: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = summary,
        style = ScribbleFitTheme.typography.bodyMedium,
        modifier = modifier
    )
}

@Composable
internal fun ExerciseStats(
    exercise: ExerciseUiModel,
    modifier: Modifier = Modifier
) {
    if (!exercise.hasStats) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
    ) {
        if (exercise.estimated1RM != null || exercise.intensity != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
                exercise.estimated1RM?.let { oneRm ->
                    StatCard(
                        label = "EST. 1RM",
                        value = oneRm,
                        modifier = Modifier.weight(1f)
                    )
                }
                exercise.intensity?.let { intensity ->
                    StatCard(
                        label = "INTENSITY",
                        value = intensity,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        exercise.improvement?.let { improvement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = improvement.uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
        }
    }
}

@Composable
internal fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainerLow,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.smallLarger),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)) {
            Text(
                text = label.uppercase(),
                style = ScribbleFitTheme.typography.labelMedium,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                style = ScribbleFitTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
