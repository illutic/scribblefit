package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.canvas.ui.R

@Composable
internal fun ScribbleInputPill(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.canvas_textfield_placeholder),
) {
    val colors = ScribbleFitTheme.colors
    val spacing = ScribbleFitTheme.spacing
    val haptic = LocalHapticFeedback.current

    val isSendEnabled = value.isNotBlank() && !isLoading
    val workoutInputFieldLabel = stringResource(R.string.canvas_workout_input_field)
    val inputEnabledLabel = stringResource(R.string.canvas_input_enabled)
    val inputDisabledLabel = stringResource(R.string.canvas_input_disabled)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.medium)
            .padding(bottom = spacing.medium)
            .height(52.dp)
            .clip(CircleShape)
            .background(colors.softGray),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 18.dp)
                    .semantics {
                        contentDescription = workoutInputFieldLabel
                    },
                textStyle = TextStyle(
                    color = colors.richBlack,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(colors.richBlack),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (isSendEnabled) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onSendClick()
                        }
                    }
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                style = TextStyle(
                                    color = colors.midGray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            )
                        }
                        innerTextField()
                    }
                }
            )

            IconButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onSendClick()
                },
                enabled = isSendEnabled,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.softGray)
                    .semantics {
                        stateDescription = if (isSendEnabled) inputEnabledLabel else inputDisabledLabel
                    }
            ) {
                Icon(
                    imageVector = Icons.Rounded.KeyboardArrowUp,
                    contentDescription = stringResource(R.string.canvas_send_workout),
                    tint = colors.richBlack,
                    modifier = Modifier
                        .size(20.dp)
                        .alpha(if (isSendEnabled) 1f else 0.5f)
                )
            }
        }
    }
}

@Composable
@PreviewLightDark
private fun ScribbleInputPillPreview() {
    ScribbleFitTheme {
        Box(modifier = Modifier.background(ScribbleFitTheme.colors.background)) {
            ScribbleInputPill(
                value = "",
                onValueChange = {},
                onSendClick = {},
                isLoading = false
            )
        }
    }
}

@Composable
@PreviewLightDark
private fun ScribbleInputPillActivePreview() {
    ScribbleFitTheme {
        Box(modifier = Modifier.background(ScribbleFitTheme.colors.background)) {
            ScribbleInputPill(
                value = "Bench 135x5x3",
                onValueChange = {},
                onSendClick = {},
                isLoading = false
            )
        }
    }
}
