package com.scribblefit.feature.canvas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun CanvasTopBar(
    appName: String,
    modifier: Modifier = Modifier,
    onProfileClick: () -> Unit = {},
) {
    TopBar(
        modifier = modifier,
        title = {
            Text(appName)
        },
        actions = {
            ProfileIconButton(
                onClick = onProfileClick,
            )
        },
    )
}

@Composable
private fun ProfileIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Icon(
            imageVector = Icons.Rounded.Person,
            contentDescription = stringResource(R.string.canvas_profile_button),
        )
    }
}

@Composable
@PreviewLightDark
private fun CanvasTopBarPreview() {
    ScribbleFitTheme {
        CanvasTopBar(
            appName = "ScribbleFit",
        )
    }
}
