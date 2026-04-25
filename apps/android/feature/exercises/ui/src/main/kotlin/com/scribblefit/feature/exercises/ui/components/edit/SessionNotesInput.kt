package com.scribblefit.feature.exercises.ui.components.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
fun SessionNotesInput(
    notes: String,
    label: String,
    placeholder: String,
    onNotesChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = ScribbleFitTheme.spacing.small)
        )
        BasicTextField(
            value = notes,
            onValueChange = onNotesChange,
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ScribbleFitTheme.colors.surfaceContainerLow,
                    RoundedCornerShape(ScribbleFitTheme.spacing.small)
                )
                .padding(ScribbleFitTheme.spacing.medium),
            textStyle = ScribbleFitTheme.typography.bodyMedium.copy(
                color = ScribbleFitTheme.colors.primary
            ),
            cursorBrush = SolidColor(ScribbleFitTheme.colors.primary),
            decorationBox = { innerTextField ->
                if (notes.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f)
                    )
                }
                innerTextField()
            },
            minLines = 3
        )
    }
}
