package com.scribblefit.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.scribblefit.core.navigation.Screen

data class BottomBarUiItem(
    val screen: Screen,
    val icon: Painter,
    val label: String,
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomBar(
    items: List<BottomBarUiItem>,
    selectedTab: Screen,
    isVisible: Boolean,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        FloatingToolbarDefaults.vibrantFloatingToolbarColors(
            toolbarContainerColor = ScribbleFitTheme.colors.surfaceContainerLow,
            toolbarContentColor = ScribbleFitTheme.colors.primary,
        )

    HorizontalFloatingToolbar(
        expanded = isVisible,
        colors = colors,
        modifier = modifier,
        expandedShadowElevation = ScribbleFitTheme.spacing.small,
        collapsedShadowElevation = ScribbleFitTheme.spacing.small,
        contentPadding = PaddingValues(
            horizontal = ScribbleFitTheme.spacing.smallLarger,
            vertical = ScribbleFitTheme.spacing.small
        )
    ) {
        items.forEach { item ->
            BottomBarItem(
                item = item,
                isSelected = item.screen == selectedTab,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomBarUiItem,
    isSelected: Boolean,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .then(
                    if (isSelected) {
                        Modifier.background(
                            color = ScribbleFitTheme.colors.primary.copy(alpha = 0.1f),
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
                .clip(RoundedCornerShape(ScribbleFitTheme.shapes.large))
                .clickable(onClick = { onClick(item.screen) })
                .sizeIn(minWidth = 64.dp, minHeight = 48.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = item.icon,
            contentDescription = item.label,
            tint = if (isSelected) {
                ScribbleFitTheme.colors.primary
            } else {
                ScribbleFitTheme.colors.primary.copy(alpha = 0.5f)
            },
        )
    }
}

@Composable
@PreviewLightDark
private fun BottomBarPreview() {
    ScribbleFitTheme {
        BottomBar(
            items = emptyList(),
            selectedTab = Screen.Canvas(),
            isVisible = true,
            onClick = {},
        )
    }
}
