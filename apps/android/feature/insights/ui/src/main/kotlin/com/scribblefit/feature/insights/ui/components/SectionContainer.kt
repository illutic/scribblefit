package com.scribblefit.feature.insights.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun SectionContainer(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)) {
        Text(
            text = title.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        content()
    }
}
