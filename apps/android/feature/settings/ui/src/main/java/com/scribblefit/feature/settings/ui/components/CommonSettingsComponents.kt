package com.scribblefit.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        Text(
            text = title.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            modifier = Modifier.padding(horizontal = ScribbleFitTheme.spacing.small)
        )
        Surface(
            color = ScribbleFitTheme.colors.surfaceContainerLow,
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
                content = content
            )
        }
    }
}

@Composable
internal fun SettingsDivider(
    modifier: Modifier = Modifier,
    color: Color = ScribbleFitTheme.colors.surfaceContainer,
    thickness: Dp = 1.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}
