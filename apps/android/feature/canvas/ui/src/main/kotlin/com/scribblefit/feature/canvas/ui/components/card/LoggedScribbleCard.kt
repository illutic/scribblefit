package com.scribblefit.feature.canvas.ui.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.HorizontalDivider
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
internal fun LoggedScribbleCard(
    scribble: ScribbleUiModel,
    onClick: () -> Unit
) {
    ScribbleCardContainer(
        onClick = onClick,
        alpha = 0.8f
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)) {
            scribble.exercises.forEachIndexed { index, exercise ->
                Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ExerciseHeader(
                            exercise = exercise,
                            fontSize = 28,
                            kerning = -1.0,
                            modifier = Modifier.weight(1f)
                        )
                        
                        if (index == 0) {
                            Surface(
                                color = ScribbleFitTheme.colors.primary.copy(alpha = 0.05f),
                                shape = CircleShape,
                                modifier = Modifier.padding(start = ScribbleFitTheme.spacing.medium)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Check,
                                        contentDescription = null,
                                        tint = ScribbleFitTheme.colors.primary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = "LOGGED",
                                        style = ScribbleFitTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = ScribbleFitTheme.colors.primary,
                                        letterSpacing = 1.sp
                                    )
                                }
                            }
                        }
                    }
                    
                    ExerciseStats(exercise)
                    
                    if (index < scribble.exercises.size - 1) {
                        HorizontalDivider(
                            color = ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                            thickness = 0.5.dp
                        )
                    }
                }
            }
        }
    }
}
