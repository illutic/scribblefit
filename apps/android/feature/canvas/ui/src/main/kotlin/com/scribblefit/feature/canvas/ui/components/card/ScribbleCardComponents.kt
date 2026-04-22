package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.GlassCard
import com.scribblefit.feature.canvas.ui.ExerciseUiModel
import com.scribblefit.core.designsystem.components.ExerciseHeader as SharedExerciseHeader

@Composable
internal fun ScribbleStatusBadge(
    text: String,
    modifier: Modifier = Modifier,
    showIcon: Boolean = false
) {
    Row(
        modifier = modifier
            .background(
                color = ScribbleFitTheme.colors.primary.copy(alpha = ScribbleFitTheme.Alphas.badgeBackground),
                shape = CircleShape
            )
            .padding(
                horizontal = ScribbleFitTheme.spacing.smallLarger,
                vertical = ScribbleFitTheme.spacing.small
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showIcon) {
            Icon(
                imageVector = Icons.Rounded.CheckCircle,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.primary,
                modifier = Modifier
                    .size(ScribbleFitTheme.spacing.medium)
                    .padding(end = ScribbleFitTheme.spacing.extraSmall)
            )
        }
        Text(
            text = text.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = ScribbleFitTheme.colors.primary,
            letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing
        )
    }
}

@Composable
internal fun ScribbleCardContainer(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    alpha: Float = 1f,
    content: @Composable ColumnScope.() -> Unit
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        alpha = alpha,
        content = content
    )
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
internal fun ExerciseHeader(
    exercise: ExerciseUiModel,
    modifier: Modifier = Modifier,
    fontSize: Int = 24,
    kerning: Double = -0.5
) {
    SharedExerciseHeader(
        name = exercise.name,
        formattedSummary = exercise.formattedSummary,
        modifier = modifier,
        fontSize = fontSize,
        kerning = kerning
    )
}