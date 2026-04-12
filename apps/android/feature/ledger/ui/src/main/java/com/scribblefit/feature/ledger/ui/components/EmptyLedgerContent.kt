package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.History,
            contentDescription = null,
            tint = ScribbleFitTheme.colors.primary.copy(alpha = 0.4f),
            modifier = Modifier.size(80.dp)
        )
        
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        
        Text(
            text = title,
            style = ScribbleFitTheme.typography.headlineSmall,
            color = ScribbleFitTheme.colors.primary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.padding(vertical = 12.dp))
        
        Button(
            onClick = onCtaClick,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = ScribbleFitTheme.colors.primary,
                contentColor = ScribbleFitTheme.colors.onPrimary
            ),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = cta,
                style = ScribbleFitTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
