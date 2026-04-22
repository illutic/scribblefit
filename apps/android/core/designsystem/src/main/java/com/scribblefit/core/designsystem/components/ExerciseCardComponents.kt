package com.scribblefit.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.scribblefit.core.designsystem.R
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass

@Composable
fun TrainingExerciseCard(
    name: String,
    formattedSummary: String,
    estimated1RM: String?,
    intensity: String?,
    improvement: String?,
    hasStats: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    fontSize: Int = 24,
    kerning: Double = -0.5
) {
    GlassCard(
        modifier = modifier,
        onClick = onClick,
        alpha = alpha,
        contentPadding = PaddingValues(ScribbleFitTheme.spacing.extraSmall)
    ) {
        ExerciseHeader(
            name = name,
            formattedSummary = formattedSummary,
            fontSize = fontSize,
            kerning = kerning
        )
        ExerciseStats(
            estimated1RM = estimated1RM,
            intensity = intensity,
            improvement = improvement,
            hasStats = hasStats,
            estimated1RMLabel = stringResource(R.string.common_estimated_1rm),
            intensityLabel = stringResource(R.string.common_intensity)
        )
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    alpha: Float = 1f,
    contentPadding: PaddingValues = PaddingValues(ScribbleFitTheme.spacing.medium),
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(ScribbleFitTheme.shapes.medium))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .scribbleGlass(cornerRadius = ScribbleFitTheme.shapes.medium)
            .alpha(alpha)
    ) {
        Column(
            modifier = Modifier.padding(contentPadding),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
            content = content
        )
    }
}

@Composable
fun ExerciseHeader(
    name: String,
    formattedSummary: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 24,
    kerning: Double = -0.5
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraSmall)
    ) {
        Text(
            text = name,
            style = ScribbleFitTheme.typography.titleLarge.copy(
                fontSize = fontSize.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = kerning.sp
            ),
            color = ScribbleFitTheme.colors.primary
        )
        Text(
            text = formattedSummary,
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )
    }
}

@Composable
fun ExerciseStats(
    estimated1RM: String?,
    intensity: String?,
    improvement: String?,
    hasStats: Boolean,
    estimated1RMLabel: String,
    intensityLabel: String,
    modifier: Modifier = Modifier
) {
    if (!hasStats) return

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        if (estimated1RM != null || intensity != null) {
            Row(horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)) {
                estimated1RM?.let { oneRm ->
                    StatCard(
                        label = estimated1RMLabel,
                        value = oneRm,
                        modifier = Modifier.weight(1f)
                    )
                }
                intensity?.let { intensityVal ->
                    StatCard(
                        label = intensityLabel,
                        value = intensityVal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        improvement?.let { improvementVal ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraSmall)
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.size(ScribbleFitTheme.spacing.smallLarger)
                )
                Text(
                    text = improvementVal.uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing
                    ),
                    color = ScribbleFitTheme.colors.midGray
                )
            }
        }
    }
}

@Composable
fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ScribbleFitTheme.shapes.small))
            .background(ScribbleFitTheme.colors.primary.copy(alpha = ScribbleFitTheme.Alphas.cardOverlay))
            .padding(ScribbleFitTheme.spacing.smallLarger)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraSmall)) {
            Text(
                text = label.uppercase(),
                style = ScribbleFitTheme.typography.labelSmall,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold,
                letterSpacing = ScribbleFitTheme.typography.labelSmall.letterSpacing
            )
            Text(
                text = value,
                style = ScribbleFitTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.primary
            )
        }
    }
}
