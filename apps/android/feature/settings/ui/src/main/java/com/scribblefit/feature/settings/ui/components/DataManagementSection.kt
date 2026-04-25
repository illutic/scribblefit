package com.scribblefit.feature.settings.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.settings.ui.SettingsDivider
import com.scribblefit.feature.settings.ui.SettingsSection
import com.scribblefit.feature.settings.ui.SettingsState

@Composable
internal fun DataManagementSection(
    state: SettingsState,
    onExport: () -> Unit,
    onClearData: () -> Unit
) {
    SettingsSection(title = state.dataTitle) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onExport() }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = state.exportWorkoutLedgerLabel,
                    style = ScribbleFitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = ScribbleFitTheme.colors.primary
                )
                Text(
                    text = state.jsonFormatLabel,
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            Icon(
                imageVector = Icons.Rounded.Download,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.midGray,
                modifier = Modifier.size(20.dp)
            )
        }

        SettingsDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClearData() }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.clearAllDataLabel,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.dangerRed,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Rounded.DeleteForever,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.dangerRed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
