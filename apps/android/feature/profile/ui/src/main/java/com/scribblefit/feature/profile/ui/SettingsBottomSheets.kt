package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing
import com.scribblefit.feature.ai.domain.model.LLMProvider
import com.scribblefit.feature.profile.domain.model.ThemePreference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProviderBottomSheet(
    currentProvider: LLMProvider,
    onProviderSelected: (LLMProvider) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitColors.Background
    ) {
        LazyColumn(modifier = Modifier.padding(bottom = ScribbleFitSpacing.Large)) {
            item {
                Text(
                    text = "Provider",
                    fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScribbleFitColors.MidGray,
                    modifier = Modifier.padding(
                        horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                        vertical = ScribbleFitSpacing.Small
                    )
                )
                RowDivider()
            }
            items(LLMProvider.entries) { provider ->
                val isSelected = provider == currentProvider
                TextButton(
                    onClick = { onProviderSelected(provider) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = provider.displayName(),
                        fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                        color = if (isSelected) ScribbleFitColors.RichBlack else ScribbleFitColors.MidGray,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ModelBottomSheet(
    models: List<String>,
    onModelSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitColors.Background
    ) {
        LazyColumn(modifier = Modifier.padding(bottom = ScribbleFitSpacing.Large)) {
            item {
                Text(
                    text = "Model",
                    fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScribbleFitColors.MidGray,
                    modifier = Modifier.padding(
                        horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                        vertical = ScribbleFitSpacing.Small
                    )
                )
                RowDivider()
            }
            items(models) { model ->
                TextButton(
                    onClick = { onModelSelected(model) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = model,
                        fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                        color = ScribbleFitColors.RichBlack,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ThemeBottomSheet(
    currentTheme: ThemePreference,
    onThemeSelected: (ThemePreference) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitColors.Background
    ) {
        LazyColumn(modifier = Modifier.padding(bottom = ScribbleFitSpacing.Large)) {
            item {
                Text(
                    text = "Theme",
                    fontSize = ROW_VALUE_FONT_SIZE_SP.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ScribbleFitColors.MidGray,
                    modifier = Modifier.padding(
                        horizontal = ROW_HORIZONTAL_PADDING_DP.dp,
                        vertical = ScribbleFitSpacing.Small
                    )
                )
                RowDivider()
            }
            items(ThemePreference.entries) { theme ->
                val isSelected = theme == currentTheme
                TextButton(
                    onClick = { onThemeSelected(theme) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = theme.name.replaceFirstChar { it.uppercase() },
                        fontSize = ROW_LABEL_FONT_SIZE_SP.sp,
                        color = if (isSelected) ScribbleFitColors.RichBlack else ScribbleFitColors.MidGray,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
