package com.scribblefit.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitShapes
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing

@Composable
fun ScribbleFitButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        enabled = enabled,
        shape = ScribbleFitShapes.Large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        ),
        content = content
    )
}

@Composable
fun ScribbleFitCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = ScribbleFitShapes.Large,
        color = containerColor,
        content = {
            Column(
                modifier = Modifier.padding(ScribbleFitSpacing.Medium),
                content = content
            )
        }
    )
}

@Composable
fun ScribbleFitPill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        shape = ScribbleFitShapes.Large,
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = ScribbleFitSpacing.Medium, vertical = ScribbleFitSpacing.Small),
            style = MaterialTheme.typography.titleSmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            )
        )
    }
}

@Composable
fun ScribbleFitTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Surface(
        modifier = modifier.height(56.dp),
        shape = ScribbleFitShapes.ExtraLarge,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ScribbleFitSpacing.Medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(ScribbleFitSpacing.XS))
            }

            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    )
                }
                
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(ScribbleFitSpacing.XS))
                trailingIcon()
            }
        }
    }
}
