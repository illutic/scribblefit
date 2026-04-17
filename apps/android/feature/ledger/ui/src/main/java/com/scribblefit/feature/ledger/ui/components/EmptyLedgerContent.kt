package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme

@Composable
internal fun EmptyLedgerContent(
    title: String,
    cta: String,
    onCtaClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            ScribbleFitTheme.spacing.medium,
            Alignment.CenterVertically
        )
    ) {
        item {
            Icon(
                imageVector = Icons.Rounded.History,
                contentDescription = null,
                tint = ScribbleFitTheme.colors.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(80.dp)
            )
        }

        item {
            Text(
                text = title,
                style = ScribbleFitTheme.typography.headlineSmall,
                color = ScribbleFitTheme.colors.primary,
                textAlign = TextAlign.Center
            )
        }

        item {
            Button(
                onClick = onCtaClick,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ScribbleFitTheme.colors.primary,
                    contentColor = ScribbleFitTheme.colors.onPrimary
                ),
                modifier = Modifier.padding(top = ScribbleFitTheme.spacing.small)
            ) {
                Text(
                    text = cta,
                    style = ScribbleFitTheme.typography.titleMedium,
                    modifier = Modifier.padding(
                        horizontal = ScribbleFitTheme.spacing.small,
                        vertical = 4.dp
                    )
                )
            }
        }
    }
}
