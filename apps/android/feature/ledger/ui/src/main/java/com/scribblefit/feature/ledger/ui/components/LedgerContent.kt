package com.scribblefit.feature.ledger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.ledger.ui.LedgerIntent
import com.scribblefit.feature.ledger.ui.LedgerState

@Composable
internal fun LedgerContent(
    modifier: Modifier = Modifier,
    state: LedgerState,
    onIntent: (LedgerIntent) -> Unit,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = ScribbleFitTheme.spacing.large,
            vertical = ScribbleFitTheme.spacing.large
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
    ) {
        items(state.groupedScribbles, key = { it.date.toString() }) { dailyScribbles ->
            Column(
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                Text(
                    text = state.getDateHeader(dailyScribbles.date),
                    style = ScribbleFitTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.midGray
                )

                dailyScribbles.scribbles.forEach { scribble ->
                    ScribbleItem(
                        scribble = scribble,
                        scribbleBadgeLabel = state.scribbleBadgeLabel,
                        onScribbleClick = {
                            onIntent(LedgerIntent.ScribbleTapped(scribble.id))
                        },
                        onExerciseClick = { exerciseId ->
                            onIntent(LedgerIntent.NavigateToExerciseDetails(exerciseId))
                        }
                    )
                }
            }
        }
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
