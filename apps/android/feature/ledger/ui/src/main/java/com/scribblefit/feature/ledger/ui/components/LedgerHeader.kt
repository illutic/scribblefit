package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar

@Composable
internal fun LedgerHeader(
    title: String,
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopBar(
        modifier = modifier.fillMaxWidth(),
        title = {
            Text(
                text = title,
                style = ScribbleFitTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        },
        actions = {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    imageVector = Icons.Rounded.Refresh,
                    contentDescription = "Refresh",
                    tint = ScribbleFitTheme.colors.primary
                )
            }
        }
    )
}
