package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing

internal const val TITLE_FONT_SIZE_SP = 28
internal const val SECTION_HEADER_FONT_SIZE_SP = 12
internal const val ROW_LABEL_FONT_SIZE_SP = 17
internal const val ROW_VALUE_FONT_SIZE_SP = 15
internal const val SEGMENT_FONT_SIZE_SP = 13
internal const val PILL_FONT_SIZE_SP = 15

internal const val TITLE_TOP_MARGIN_DP = 24
internal const val FIRST_SECTION_TOP_DP = 24
internal const val SUBSEQUENT_SECTION_TOP_DP = 32
internal const val SECTION_HORIZONTAL_MARGIN_DP = 16
internal const val ROW_HORIZONTAL_PADDING_DP = 16
internal const val ROW_VERTICAL_PADDING_DP = 16
internal const val DIVIDER_HEIGHT_DP = 1
internal const val SEGMENT_CORNER_DP = 6
internal const val PILL_CORNER_DP = 8
internal const val PILL_BORDER_DP = 1
internal const val PILL_HORIZONTAL_PADDING_DP = 12
internal const val PILL_VERTICAL_PADDING_DP = 6

internal fun LLMProvider.displayName(): String = when (this) {
    LLMProvider.GEMINI -> "Gemini"
    LLMProvider.OPENAI -> "OpenAI"
    LLMProvider.LOCAL -> "Local"
}

@Composable
internal fun SectionHeader(title: String) {
    Column {
        Text(
            text = title,
            fontSize = SECTION_HEADER_FONT_SIZE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScribbleFitColors.MidGray,
            modifier = Modifier.padding(
                start = SECTION_HORIZONTAL_MARGIN_DP.dp,
                end = SECTION_HORIZONTAL_MARGIN_DP.dp,
                bottom = ScribbleFitSpacing.Small
            )
        )
        RowDivider()
    }
}

@Composable
internal fun SettingsRow(
    label: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                vertical = ROW_VERTICAL_PADDING_DP.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
            color = ScribbleFitColors.RichBlack
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.MidGray
            )
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = ScribbleFitColors.MidGray
            )
        }
    }
    RowDivider()
}

@Composable
internal fun ApiKeyRow(
    showInput: Boolean,
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onPillClicked: () -> Unit,
    onConfirm: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                    vertical = ROW_VERTICAL_PADDING_DP.dp
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "API Key",
                fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.RichBlack
            )
            Spacer(modifier = Modifier.weight(1f))
            val maskedDisplay = "\u2022\u2022\u2022\u2022"
            Text(
                text = maskedDisplay,
                fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                color = ScribbleFitColors.MidGray,
                modifier = Modifier.padding(end = ScribbleFitSpacing.Small)
            )
            SaveKeyPill(onClick = onPillClicked)
        }
        if (showInput) {
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = { Text(text = "Paste your API key") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ROW_HORIZONTAL_PADDING_DP.dp)
                    .padding(bottom = ScribbleFitSpacing.Small)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ROW_HORIZONTAL_PADDING_DP.dp)
                    .padding(bottom = ROW_VERTICAL_PADDING_DP.dp)
                    .clip(RoundedCornerShape(PILL_CORNER_DP.dp))
                    .background(ScribbleFitColors.SoftGray)
                    .border(
                        PILL_BORDER_DP.dp,
                        ScribbleFitColors.LightGray,
                        RoundedCornerShape(PILL_CORNER_DP.dp)
                    )
                    .clickable(onClick = onConfirm)
                    .padding(vertical = ScribbleFitSpacing.Small),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Confirm",
                    fontSize = PILL_FONT_SIZE_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScribbleFitColors.RichBlack
                )
            }
        }
        RowDivider()
    }
}

@Composable
internal fun SaveKeyPill(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(PILL_CORNER_DP.dp))
            .background(ScribbleFitColors.SoftGray)
            .border(
                width = PILL_BORDER_DP.dp,
                color = ScribbleFitColors.LightGray,
                shape = RoundedCornerShape(PILL_CORNER_DP.dp)
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = PILL_HORIZONTAL_PADDING_DP.dp,
                vertical = PILL_VERTICAL_PADDING_DP.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Save Key",
            fontSize = PILL_FONT_SIZE_SP.sp,
            fontWeight = FontWeight.SemiBold,
            color = ScribbleFitColors.RichBlack
        )
    }
}

@Composable
internal fun WeightUnitToggle(
    selected: Weight,
    onUnitSelected: (Weight) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(SEGMENT_CORNER_DP.dp))
            .background(ScribbleFitColors.SoftGray),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Weight.entries.forEach { unit ->
            val isActive = unit == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(SEGMENT_CORNER_DP.dp))
                    .background(if (isActive) ScribbleFitColors.RichBlack else Color.Transparent)
                    .clickable { onUnitSelected(unit) }
                    .padding(
                        horizontal = PILL_HORIZONTAL_PADDING_DP.dp,
                        vertical = PILL_VERTICAL_PADDING_DP.dp
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = unit.name.lowercase(),
                    fontSize = SEGMENT_FONT_SIZE_SP.sp,
                    fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isActive) Color.White else ScribbleFitColors.MidGray
                )
            }
        }
    }
}

@Composable
internal fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DIVIDER_HEIGHT_DP.dp)
            .background(ScribbleFitColors.LightGray)
    )
}
