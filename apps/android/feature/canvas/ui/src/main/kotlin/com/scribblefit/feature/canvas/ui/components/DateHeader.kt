package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun DateHeader(
    currentDate: String,
    isCurrentDate: Boolean,
    modifier: Modifier = Modifier,
    onPreviousDayClick: () -> Unit = {},
    onNextDayClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PreviousDayButton(
            onClick = onPreviousDayClick,
            enabled = true,
        )
        DateText(currentDate)
        NextDayButton(
            onClick = onNextDayClick,
            enabled = !isCurrentDate,
        )
    }
}

@Composable
private fun PreviousDayButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors =
        IconButtonDefaults.iconButtonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ScribbleFitTheme.colors.midGray,
            containerColor = Color.Transparent,
            contentColor = ScribbleFitTheme.colors.richBlack,
        )
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.canvas_previous_day),
        )
    }
}

@Composable
private fun NextDayButton(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors =
        IconButtonDefaults.iconButtonColors(
            disabledContainerColor = Color.Transparent,
            disabledContentColor = ScribbleFitTheme.colors.midGray,
            containerColor = Color.Transparent,
            contentColor = ScribbleFitTheme.colors.richBlack,
        )
    IconButton(
        onClick = onClick,
        enabled = enabled,
        colors = colors,
        modifier = modifier,
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = stringResource(R.string.canvas_next_day),
        )
    }
}

@Composable
private fun DateText(
    date: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = date,
        style = ScribbleFitTheme.typography.labelMedium,
        color = ScribbleFitTheme.colors.midGray,
        modifier = modifier,
    )
}

@Composable
@PreviewLightDark
private fun DateHeaderPreview() {
    ScribbleFitTheme {
        DateHeader(
            currentDate = "June 24, 2024",
            isCurrentDate = true,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
