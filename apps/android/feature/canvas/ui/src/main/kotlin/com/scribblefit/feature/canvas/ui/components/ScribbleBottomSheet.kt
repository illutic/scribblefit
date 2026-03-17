package com.scribblefit.feature.canvas.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.Scribble
import com.scribblefit.feature.canvas.ui.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ScribbleBottomSheet(
    scribble: Scribble,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val weightUnit = stringResource(R.string.canvas_weight_unit_kg)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ScribbleFitTheme.colors.background,
        contentColor = ScribbleFitTheme.colors.richBlack,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.canvas_dialog_title),
                style = ScribbleFitTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scribble.rawText,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.strongGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                items(scribble.exercises) { exercise ->
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        Text(
                            text = exercise.canonicalName,
                            style = ScribbleFitTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        exercise.sets.forEach { set ->
                            Text(
                                text = "${set.reps} x ${set.weight} $weightUnit",
                                style = ScribbleFitTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.canvas_dialog_delete),
                        color = ScribbleFitTheme.colors.dangerRed
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.canvas_dialog_edit))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.canvas_dialog_confirm))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
