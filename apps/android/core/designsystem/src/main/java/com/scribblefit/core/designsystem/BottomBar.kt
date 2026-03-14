package com.scribblefit.core.designsystem

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.scribblefit.core.navigation.BottomBarItem
import com.scribblefit.core.navigation.BottomBarState
import com.scribblefit.core.navigation.Screen


// TODO - Replace with actual icons
private val BottomBarItem.icon: Painter
    @Composable
    get() = when (screen) {
        Screen.Canvas -> rememberVectorPainter(Icons.Rounded.Home)
        Screen.Insights -> rememberVectorPainter(Icons.Rounded.Star)
        Screen.Ledger -> rememberVectorPainter(Icons.Rounded.Person)
        else -> {
            throw IllegalArgumentException("No icon defined for screen: $screen")
        }
    }

// TODO - Replace with actual strings from resources
private val BottomBarItem.string: String
    @Composable
    get() = when (screen) {
        Screen.Canvas -> "Canvas"
        Screen.Insights -> "Insights"
        Screen.Ledger -> "Ledger"
        else -> {
            throw IllegalArgumentException("No string defined for screen: $screen")
        }
    }

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AppBottomBar(
    bottomBarState: BottomBarState,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    HorizontalFloatingToolbar(
        expanded = bottomBarState.isVisible,
        modifier = modifier.padding(ScribbleFitTheme.spacing.screenPadding)
    ) {
        bottomBarState.items.forEach { item ->
            BottomBarItem(
                item = item,
                isSelected = item.screen == bottomBarState.selectedTab,
                onClick = onClick,
                modifier = Modifier.padding(horizontal = ScribbleFitTheme.spacing.medium)
            )
        }
    }
}

@Composable
private fun BottomBarItem(
    item: BottomBarItem,
    isSelected: Boolean,
    onClick: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(ScribbleFitTheme.shapes.large))
            .clickable(onClick = { onClick(item.screen) })
            .padding(ScribbleFitTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = item.icon,
            contentDescription = item.string,
            tint = if (isSelected) ScribbleFitTheme.colors.richBlack else ScribbleFitTheme.colors.midGray
        )
        Text(
            text = item.string,
            style = ScribbleFitTheme.typography.labelMedium,
            color = if (isSelected) ScribbleFitTheme.colors.richBlack else ScribbleFitTheme.colors.midGray
        )
    }
}

@Composable
@PreviewLightDark
private fun AppBottomBarPreview() {
    ScribbleFitTheme {
        AppBottomBar(
            bottomBarState = BottomBarState(),
            onClick = {}
        )
    }
}