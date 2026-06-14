package com.scribblefit.feature.settings.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.settings.ui.R
import com.scribblefit.feature.settings.ui.SettingsDivider
import com.scribblefit.feature.settings.ui.SettingsSection
import com.scribblefit.feature.settings.ui.SettingsState

@Composable
internal fun AppearanceSection(
    state: SettingsState,
    onThemeChange: (ThemePreference) -> Unit,
    onDynamicThemeToggle: (Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsSection(title = state.appearanceTitle) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = state.themeTitle,
                style = ScribbleFitTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            Box {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = state.themeLabel,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.midGray
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.settings_theme_dropdown_icon),
                        tint = ScribbleFitTheme.colors.midGray,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(ScribbleFitTheme.colors.surfaceContainerLow)
                ) {
                    DropdownMenuItem(
                        text = { Text(state.themeLightLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.LIGHT)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(state.themeDarkLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.DARK)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(state.themeSystemLabel) },
                        onClick = {
                            onThemeChange(ThemePreference.SYSTEM)
                            expanded = false
                        }
                    )
                }
            }
        }

        SettingsDivider()

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.settings_dynamic_theme),
                    style = ScribbleFitTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = stringResource(R.string.settings_dynamic_theme_description),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            Switch(
                checked = state.isDynamicTheme,
                onCheckedChange = onDynamicThemeToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = ScribbleFitTheme.colors.onPrimary,
                    checkedTrackColor = ScribbleFitTheme.colors.primary,
                    uncheckedThumbColor = ScribbleFitTheme.colors.midGray,
                    uncheckedTrackColor = ScribbleFitTheme.colors.surfaceContainer
                )
            )
        }
    }
}
