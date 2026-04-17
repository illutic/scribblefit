package com.scribblefit.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.scribbleGlass(cornerRadius: Dp): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.4f))
    .border(
        width = 1.dp,
        color = ScribbleFitTheme.colors.surfaceContainerLowest.copy(alpha = 0.2f),
        shape = RoundedCornerShape(cornerRadius)
    )
