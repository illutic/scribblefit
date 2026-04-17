package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun CanvasTopBar(
    dateString: String,
    onPreviousDayClick: () -> Unit,
    onNextDayClick: () -> Unit,
    onDateClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Surface(
        color = ScribbleFitTheme.colors.surface.copy(alpha = 0.8f),
        modifier = Modifier.fillMaxWidth()
    ) {
        TopBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = onPreviousDayClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronLeft,
                            contentDescription = stringResource(R.string.canvas_previous_day),
                            tint = ScribbleFitTheme.colors.midGray
                        )
                    }
                    Text(
                        text = dateString,
                        style = ScribbleFitTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable(onClick = onDateClick)
                            .padding(horizontal = ScribbleFitTheme.spacing.small)
                    )
                    IconButton(onClick = onNextDayClick, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Rounded.ChevronRight,
                            contentDescription = stringResource(R.string.canvas_next_day),
                            tint = ScribbleFitTheme.colors.midGray
                        )
                    }
                }
            },
            actions = {
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = stringResource(R.string.canvas_settings_button),
                        tint = ScribbleFitTheme.colors.primary
                    )
                }
            }
        )
    }
}
