package com.scribblefit.core.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> SegmentedSelector(
    options: List<Pair<T, String>>,
    selectedOption: T,
    onOptionSelected: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = ScribbleFitTheme.colors.surfaceContainer,
        shape = CircleShape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            options.forEach { (option, label) ->
                val isSelected = option == selectedOption
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(CircleShape)
                        .clickable { onOptionSelected(option) },
                    color = if (isSelected) ScribbleFitTheme.colors.surfaceContainerLowest else Color.Transparent,
                    shape = CircleShape,
                    shadowElevation = if (isSelected) 1.dp else 0.dp
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.padding(vertical = 6.dp),
                        style = ScribbleFitTheme.typography.labelMedium,
                        textAlign = TextAlign.Center,
                        color = if (isSelected) ScribbleFitTheme.colors.primary else ScribbleFitTheme.colors.midGray,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
