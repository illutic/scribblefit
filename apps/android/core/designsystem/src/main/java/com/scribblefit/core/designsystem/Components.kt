package com.scribblefit.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun ScribbleFitCard(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ScribbleFitShapes.Medium))
            .background(ScribbleFitColors.SoftGray)
            .padding(ScribbleFitSpacing.Medium),
        content = content
    )
}

@Composable
fun ScribbleFitPill(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ScribbleFitShapes.Large))
            .background(ScribbleFitColors.SoftGray)
            .padding(horizontal = ScribbleFitSpacing.Medium, vertical = ScribbleFitSpacing.Small)
    ) {
        Text(
            text = text,
            color = ScribbleFitColors.RichBlack,
            fontSize = 14.sp
        )
    }
}

@Composable
fun ScribbleFitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = ""
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(ScribbleFitShapes.Medium))
            .background(ScribbleFitColors.SoftGray)
            .padding(ScribbleFitSpacing.Medium)
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = ScribbleFitColors.MidGray, fontSize = 16.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = ScribbleFitColors.RichBlack, fontSize = 16.sp)
        )
    }
}
