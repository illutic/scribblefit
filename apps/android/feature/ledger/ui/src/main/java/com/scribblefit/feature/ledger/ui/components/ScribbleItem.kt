package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.scribbleGlass
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.ledger.ui.toTimeString

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ScribbleItem(
    scribble: Scribble,
    scribbleBadgeLabel: String,
    onScribbleClick: () -> Unit,
    onExerciseClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onScribbleClick,
        shape = RoundedCornerShape(ScribbleFitTheme.spacing.medium),
        modifier = modifier
            .fillMaxWidth()
            .scribbleGlass(cornerRadius = ScribbleFitTheme.spacing.medium),
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.5f)
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = ScribbleFitTheme.colors.primary.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = scribbleBadgeLabel,
                        style = ScribbleFitTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = ScribbleFitTheme.colors.primary,
                        modifier = Modifier.padding(
                            horizontal = ScribbleFitTheme.spacing.small,
                            vertical = ScribbleFitTheme.spacing.extraSmall
                        )
                    )
                }

                Text(
                    text = scribble.createdAt.toTimeString(),
                    style = ScribbleFitTheme.typography.labelSmall,
                    color = ScribbleFitTheme.colors.midGray
                )
            }

            Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.small))

            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = ScribbleFitTheme.colors.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (scribble.exercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.small))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
                    verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
                ) {
                    scribble.exercises.forEach { exercise ->
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.1f),
                            modifier = Modifier.clickable { onExerciseClick(exercise.id) }
                        ) {
                            Text(
                                text = exercise.canonicalName,
                                style = ScribbleFitTheme.typography.labelSmall,
                                color = ScribbleFitTheme.colors.primary,
                                modifier = Modifier.padding(
                                    horizontal = ScribbleFitTheme.spacing.small,
                                    vertical = ScribbleFitTheme.spacing.extraSmall
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
