package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun ParsedScribbleCard(
    scribble: ScribbleUiModel,
    onClick: () -> Unit
) {
    ScribbleCardContainer(
        border = BorderStroke(1.dp, ScribbleFitTheme.colors.outlineVariant.copy(alpha = 0.15f)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                ScribbleRawText(text = scribble.rawText)
                scribble.exercises.forEach { exercise ->
                    ExerciseSummary(exercise.summary)
                }
            }
            Surface(
                color = ScribbleFitTheme.colors.primary,
                shape = CircleShape,
                modifier = Modifier
                    .size(40.dp)
                    .padding(start = ScribbleFitTheme.spacing.medium)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Rounded.DoneAll,
                        contentDescription = null,
                        tint = ScribbleFitTheme.colors.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        scribble.statusText?.let {
            Surface(
                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.05f),
                shape = RoundedCornerShape(ScribbleFitTheme.shapes.small),
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = it.uppercase(),
                    modifier = Modifier.padding(
                        horizontal = ScribbleFitTheme.spacing.smallLarger,
                        vertical = ScribbleFitTheme.spacing.small
                    ),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
