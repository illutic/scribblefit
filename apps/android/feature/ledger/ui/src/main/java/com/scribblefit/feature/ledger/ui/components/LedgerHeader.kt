package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar

@Composable
internal fun LedgerHeader(
    title: String,
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
        }
    )
}
