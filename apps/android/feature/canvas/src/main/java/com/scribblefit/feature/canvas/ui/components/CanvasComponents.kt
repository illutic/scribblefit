package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.component.ScribbleFitPill
import com.scribblefit.core.designsystem.component.ScribbleFitTextField
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing

@Composable
fun CanvasHeader(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = ScribbleFitSpacing.Medium),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "EVENING, ${userName.uppercase()}",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        )
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun ContextualInsightCard(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall.copy(
            color = MaterialTheme.colorScheme.onBackground,
            lineHeight = 32.sp
        )
    )
}

@Composable
fun QuickActionPills(pills: List<String>) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(pills) { pill ->
            ScribbleFitPill(
                text = pill,
                onClick = { }
            )
        }
    }
}

@Composable
fun ScribbleInputPill(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isSyncing: Boolean
) {
    ScribbleFitTextField(
        value = text,
        onValueChange = onTextChange,
        placeholder = "Message ScribbleFit...",
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Box {
                if (text.isNotBlank()) {
                    IconButton(
                        onClick = onSubmit,
                        enabled = !isSyncing,
                        modifier = Modifier.size(32.dp)
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Submit",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                } else {
                    IconButton(
                        onClick = { /* Mic action */ },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Text("🎙️", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    )
}
