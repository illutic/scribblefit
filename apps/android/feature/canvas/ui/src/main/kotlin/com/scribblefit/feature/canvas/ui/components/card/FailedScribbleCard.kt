package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.PriorityHigh
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
import com.scribblefit.feature.canvas.ui.CanvasIntent
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.ScribbleUiModel

@Composable
internal fun FailedScribbleCard(
    state: CanvasState,
    scribble: ScribbleUiModel,
    onIntent: (CanvasIntent) -> Unit
) {
    ScribbleCardContainer {
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
                    verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ScribbleRawText(
                            text = scribble.rawText,
                            style = ScribbleFitTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Error,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.dangerRed,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = (state.getStatusText(scribble.status) ?: "").uppercase(),
                            style = ScribbleFitTheme.typography.labelMedium,
                            color = ScribbleFitTheme.colors.dangerRed,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing
                        )
                    }
                }

                Surface(
                    color = ScribbleFitTheme.colors.dangerRed.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(36.dp)
                        .padding(start = ScribbleFitTheme.spacing.small)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Rounded.PriorityHigh,
                            contentDescription = null,
                            tint = ScribbleFitTheme.colors.dangerRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(
                    text = state.retryActionLabel.uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing,
                    modifier = Modifier.clickable {
                        onIntent(
                            CanvasIntent.RetryScribbleParsing(
                                scribble.scribble
                            )
                        )
                    }
                )
                Text(
                    text = state.removeActionLabel.uppercase(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = ScribbleFitTheme.typography.labelMedium.letterSpacing,
                    modifier = Modifier.clickable {
                        onIntent(
                            CanvasIntent.ShowDeleteConfirmation(
                                scribble.id
                            )
                        )
                    }
                )
            }
        }
    }
}
