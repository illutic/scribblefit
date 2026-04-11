package com.scribblefit.core.designsystem

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.scribblefit.core.navigation.BottomBarItem
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen

// TODO - Replace with actual icons
private val BottomBarItem.icon: Painter
    @Composable
    get() =
        when (screen) {
            Screen.Canvas -> {
                rememberVectorPainter(Icons.Rounded.Home)
            }

            Screen.Insights -> {
                rememberVectorPainter(Icons.Rounded.Star)
            }

            Screen.Ledger -> {
                rememberVectorPainter(Icons.Rounded.Person)
            }

            Screen.Profile -> {
                rememberVectorPainter(Icons.Rounded.Person)
            }

            Screen.Settings -> {
                rememberVectorPainter(Icons.Rounded.Settings)
            }
        }

// TODO - Replace with actual strings from resources
private val BottomBarItem.string: String
    @Composable
    get() =
        when (screen) {
            Screen.Canvas -> {
                "Canvas"
            }

            Screen.Insights -> {
                "Insights"
            }

            Screen.Ledger -> {
                "Ledger"
            }

            Screen.Profile -> {
                "Profile"
            }

            Screen.Settings -> {
                "Settings"
            }
        }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BottomBar(
    bottomBarState: BottomBarState,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors =
        FloatingToolbarDefaults.vibrantFloatingToolbarColors(
            toolbarContainerColor = ScribbleFitTheme.colors.surfaceContainerLow,
        )

    HorizontalFloatingToolbar(
        expanded = bottomBarState.isVisible,
        colors = colors,
        modifier = modifier,
        expandedShadowElevation = 8.dp,
        collapsedShadowElevation = 8.dp,
    ) {
        bottomBarState.items.forEach { item ->
            BottomBarItem(
                item = item,
                isSelected = item.screen == bottomBarState.selectedTab,
                onClick = onClick,
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .clip(RoundedCornerShape(ScribbleFitTheme.shapes.large))
                .clickable(onClick = { onClick(item.screen) })
                .padding(horizontal = ScribbleFitTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = item.icon,
            contentDescription = item.string,
            tint = if (isSelected) ScribbleFitTheme.colors.primary else ScribbleFitTheme.colors.midGray,
        )
    }
}

@Composable
@PreviewLightDark
private fun BottomBarPreview() {
    ScribbleFitTheme {
        BottomBar(
            bottomBarState = BottomBarState(),
            onClick = {},
        )
    }
}
