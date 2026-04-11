package com.scribblefit.feature.canvas.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.KeyboardArrowDown
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
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.BottomBar
import com.scribblefit.core.designsystem.ScribbleFitTheme

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
            .background(gradientBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isWide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
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
                    bottomBarState = state.bottomBarState,
                    onClick = { onIntent(CanvasIntent.NavigateToScreen(it)) }
                )
            }
        } else {
            AnimatedContent(
                targetState = state.isInputExpanded,
                transitionSpec = {
                    fadeIn(tween(300)) togetherWith fadeOut(tween(300))
                },
                label = "InputExpansion"
            ) { expanded ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        12.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!expanded) {
                        BottomBar(
                            bottomBarState = state.bottomBarState,
                            onClick = { onIntent(CanvasIntent.NavigateToScreen(it)) }
                        )
                    }

                    if (expanded) {
                        CanvasInputFooter(
                            initialText = state.currentScribbleText,
                            onTextChange = { onIntent(CanvasIntent.UpdateScribbleText(it)) },
                            onSendClick = { onIntent(CanvasIntent.AddScribble(state.currentScribbleText)) },
                            placeholder = state.textfieldPlaceholder,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Surface(
                        color = ScribbleFitTheme.colors.surfaceContainerLow,
                        shape = CircleShape,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .size(56.dp)
                            .clickable { onIntent(CanvasIntent.ToggleInputExpansion) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = if (expanded) Icons.Rounded.KeyboardArrowDown else Icons.Rounded.Edit,
                                contentDescription = if (expanded) {
                                    state.collapseContentDescription
                                } else {
                                    state.expandSearchContentDescription
                                },
                                tint = ScribbleFitTheme.colors.primary
                            )
                        }
                    }
                }
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
    var text by remember { mutableStateOf(initialText) }

    Surface(
        modifier = modifier,
        color = ScribbleFitTheme.colors.surfaceContainerLow.copy(alpha = 0.9f),
        shape = CircleShape,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
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
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
