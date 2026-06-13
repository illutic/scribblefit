package com.scribblefit.feature.exercises.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.ui.R

@Composable
fun SetsSection(
    sets: List<Set>,
    weightUnit: String
) {
    if (sets.isEmpty()) return

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ScribbleFitTheme.colors.surfaceContainerLowest,
                    shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium)
                )
                .padding(ScribbleFitTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
        ) {
            sets.forEach { set ->
                SetItemRow(set = set, weightUnit = weightUnit)
            }
        }
    }
}

@Composable
private fun SetItemRow(
    set: Set,
    weightUnit: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
        ) {
            Text(
                text = "${set.setNumber}.",
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.midGray,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if ((set.weight ?: 0f) > 0f) "${set.weight} $weightUnit" else "-",
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )
            
            if (set.reps > 0) {
                Text(
                    text = "x",
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
                Text(
                    text = "${set.reps}",
                    style = ScribbleFitTheme.typography.bodyLarge,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        if (set.rpe != null) {
            Text(
                text = "RPE ${set.rpe}",
                style = ScribbleFitTheme.typography.bodySmall,
                color = ScribbleFitTheme.colors.midGray
            )
        }
    }
}
