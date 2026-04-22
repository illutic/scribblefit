package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
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
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun ParsedScribbleCard(
    state: CanvasState,
    scribble: ScribbleUiModel,
    onClick: () -> Unit
) {
    ScribbleCardContainer(
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
                ) {
                    ScribbleRawText(text = scribble.rawText)

                    scribble.exercises.forEach { exercise ->
                        ExerciseHeader(exercise = exercise)
                    }
                }

                Surface(
                    color = ScribbleFitTheme.colors.primary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(start = ScribbleFitTheme.spacing.small)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircle,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.onPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            state.getStatusText(scribble.status)?.let {
                Surface(
                    color = ScribbleFitTheme.colors.primary.copy(alpha = ScribbleFitTheme.Alphas.badgeBackground),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it.uppercase(),
                        modifier = Modifier.padding(
                            horizontal = 12.dp,
                            vertical = 8.dp
                        ),
                        style = ScribbleFitTheme.typography.labelMedium,
                        color = ScribbleFitTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing
                    )
                }
            }
        }
    }
}
