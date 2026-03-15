package com.scribblefit.core.designsystem

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = {},
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        modifier = modifier,
        title = title,
        navigationIcon = navigationIcon,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = ScribbleFitTheme.colors.background,
            scrolledContainerColor = ScribbleFitTheme.colors.lightGray,
            navigationIconContentColor = ScribbleFitTheme.colors.richBlack,
            titleContentColor = ScribbleFitTheme.colors.richBlack,
            actionIconContentColor = ScribbleFitTheme.colors.richBlack
        ),
        actions = actions
    )
}

@Composable
@PreviewLightDark
private fun TopBarPreview() {
    ScribbleFitTheme {
        TopBar(
            title = { Text("Top Bar") }
        )
    }
}