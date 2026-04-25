package com.scribblefit.feature.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun SettingsFooter(state: SettingsState, version: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
    ) {
        Surface(
            color = ScribbleFitTheme.colors.surfaceContainer,
            shape = RoundedCornerShape(ScribbleFitTheme.spacing.smallLarger),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = ScribbleFitTheme.colors.midGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = state.getVersionLabel(version),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            letterSpacing = 1.sp
        )
        Text(
            text = state.copyrightLabel,
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray.copy(alpha = 0.5f)
        )
    }
}
