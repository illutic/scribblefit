package com.scribblefit.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar

@Composable
internal fun SettingsTopBar(
    onBackClick: () -> Unit,
) {
    TopBar(
        modifier = Modifier.background(ScribbleFitTheme.colors.surfaceContainerLowest),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.settings_cancel)
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = ScribbleFitTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    )
}
