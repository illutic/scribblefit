package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.BottomBar
import com.scribblefit.core.designsystem.BottomBarUiItem
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasIntent
import com.scribblefit.feature.canvas.ui.CanvasState
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun CanvasFooter(
    state: CanvasState,
    onIntent: (CanvasIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val containerDpSize = LocalWindowInfo.current.containerDpSize
    val isWide = containerDpSize.width > 600.dp
    val gradientColors = listOf(
        Color.Transparent,
        ScribbleFitTheme.colors.surface
    )
    val gradientBrush = Brush.verticalGradient(colors = gradientColors)

    Box(
        modifier = modifier
            .imePadding()
            .background(gradientBrush)
            .padding(ScribbleFitTheme.spacing.medium),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
        ) {
            val bottomBarItems = state.bottomBarState.items.map { item ->
                val icon = when (item.screen) {
                    is Screen.Canvas -> Icons.Rounded.Home
                    Screen.Insights -> Icons.Rounded.AutoGraph
                    Screen.Ledger -> Icons.Rounded.CalendarMonth
                    Screen.Settings -> Icons.Rounded.Settings
                    else -> Icons.Rounded.Home
                }
                val label = when (item.screen) {
                    is Screen.Canvas -> stringResource(R.string.nav_canvas)
                    Screen.Insights -> stringResource(R.string.nav_insights)
                    Screen.Ledger -> stringResource(R.string.nav_ledger)
                    Screen.Settings -> stringResource(R.string.nav_settings)
                    else -> ""
                }
                BottomBarUiItem(
                    screen = item.screen,
                    icon = rememberVectorPainter(icon),
                    label = label
                )
            }

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        ScribbleFitTheme.spacing.medium,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CanvasInputFooter(
                        initialText = state.currentScribbleText,
                        onTextChange = { onIntent(CanvasIntent.UpdateScribbleText(it)) },
                        onSendClick = { onIntent(CanvasIntent.AddScribble(state.currentScribbleText)) },
                        placeholder = state.textfieldPlaceholder,
                        modifier = Modifier.widthIn(max = 300.dp)
                    )
                    BottomBar(
                        items = bottomBarItems,
                        selectedTab = state.bottomBarState.selectedTab,
                        isVisible = state.bottomBarState.isVisible,
                        onClick = { onIntent(CanvasIntent.NavigateToScreen(it)) }
                    )
                    CanvasAddExerciseButton(
                        onClick = { onIntent(CanvasIntent.ShowAddExerciseSheet) }
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CanvasInputFooter(
                        initialText = state.currentScribbleText,
                        onTextChange = { onIntent(CanvasIntent.UpdateScribbleText(it)) },
                        onSendClick = { onIntent(CanvasIntent.AddScribble(state.currentScribbleText)) },
                        placeholder = state.textfieldPlaceholder,
                        modifier = Modifier.weight(1f)
                    )

                    CanvasAddExerciseButton(
                        onClick = { onIntent(CanvasIntent.ShowAddExerciseSheet) }
                    )
                }

                BottomBar(
                    items = bottomBarItems,
                    selectedTab = state.bottomBarState.selectedTab,
                    isVisible = state.bottomBarState.isVisible,
                    onClick = { onIntent(CanvasIntent.NavigateToScreen(it)) }
                )
            }
        }
    }
}

@Composable
private fun CanvasInputFooter(
    initialText: String,
    onTextChange: (String) -> Unit,
    onSendClick: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var text by remember(initialText) { mutableStateOf(initialText) }

    Surface(
        modifier = modifier,
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
        shape = CircleShape,
        shadowElevation = ScribbleFitTheme.spacing.small
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = ScribbleFitTheme.spacing.small,
                vertical = 2.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    onTextChange(it)
                },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        text = placeholder,
                        style = ScribbleFitTheme.typography.bodyMedium,
                        color = ScribbleFitTheme.colors.midGray,
                        fontWeight = FontWeight.Medium
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                textStyle = ScribbleFitTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() })
            )
            IconButton(
                onClick = onSendClick,
                enabled = text.isNotBlank(),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = ScribbleFitTheme.colors.primary,
                    contentColor = ScribbleFitTheme.colors.onPrimary,
                    disabledContainerColor = ScribbleFitTheme.colors.surfaceContainer,
                    disabledContentColor = ScribbleFitTheme.colors.midGray
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowUpward,
                    contentDescription = stringResource(R.string.canvas_send_workout),
                    modifier = Modifier.size(ScribbleFitTheme.spacing.medium),
                )
            }
        }
    }
}

@Composable
private fun CanvasAddExerciseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .padding(ScribbleFitTheme.spacing.small)
            .size(48.dp),
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = ScribbleFitTheme.colors.surfaceContainerHigh,
            contentColor = ScribbleFitTheme.colors.primary
        )
    ) {
        Icon(
            imageVector = Icons.Rounded.EditNote,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }
}
