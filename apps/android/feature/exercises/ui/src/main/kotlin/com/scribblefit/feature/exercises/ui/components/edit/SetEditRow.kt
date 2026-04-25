package com.scribblefit.feature.exercises.ui.components.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Set

@Composable
fun SetEditRow(
    set: Set,
    weightUnitLabel: String,
    setRepsSeparator: String,
    repsLabel: String,
    deleteSetContentDescription: String,
    onUpdateSetWeight: (Long, String) -> Unit,
    onUpdateSetReps: (Long, String) -> Unit,
    onDeleteSet: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var weightText by remember(set.id, set.weight) {
        mutableStateOf(set.weight?.toString() ?: "0")
    }
    var repsText by remember(set.id, set.reps) {
        mutableStateOf(set.reps.toString())
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${set.setNumber}.",
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )
        BasicTextField(
            value = weightText,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                    weightText = newValue
                    onUpdateSetWeight(set.id, newValue)
                }
            },
            modifier = Modifier.width(64.dp),
            textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            cursorBrush = SolidColor(ScribbleFitTheme.colors.primary)
        )
        Text(
            text = weightUnitLabel,
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )
        Text(
            text = setRepsSeparator,
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )
        BasicTextField(
            value = repsText,
            onValueChange = { newValue ->
                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                    repsText = newValue
                    onUpdateSetReps(set.id, newValue)
                }
            },
            modifier = Modifier.width(48.dp),
            textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                color = ScribbleFitTheme.colors.primary,
                fontWeight = FontWeight.Bold
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            cursorBrush = SolidColor(ScribbleFitTheme.colors.primary)
        )
        Text(
            text = repsLabel,
            style = ScribbleFitTheme.typography.bodyMedium,
            color = ScribbleFitTheme.colors.midGray
        )
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = { onDeleteSet(set.id) }) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = deleteSetContentDescription,
                tint = ScribbleFitTheme.colors.dangerRed,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
