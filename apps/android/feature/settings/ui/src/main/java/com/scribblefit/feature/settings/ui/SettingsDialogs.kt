package com.scribblefit.feature.settings.ui

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun ClearDataConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.settings_clear_data_confirm_title),
                style = ScribbleFitTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.settings_clear_data_confirm_message),
                style = ScribbleFitTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = ScribbleFitTheme.colors.dangerRed),
                shape = CircleShape
            ) {
                Text(stringResource(R.string.settings_clear_data_confirm_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.settings_cancel),
                    color = ScribbleFitTheme.colors.primary
                )
            }
        },
        containerColor = ScribbleFitTheme.colors.surface,
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.large)
    )
}
