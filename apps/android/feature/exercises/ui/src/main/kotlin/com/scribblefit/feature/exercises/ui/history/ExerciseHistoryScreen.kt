package com.scribblefit.feature.exercises.ui.history

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.model.ExerciseHistorySession
import com.scribblefit.feature.exercises.ui.R
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ExerciseHistoryScreen(
    state: ExerciseHistoryState,
    onIntent: (ExerciseHistoryIntent) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = state.exerciseName, 
                        style = ScribbleFitTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { onIntent(ExerciseHistoryIntent.NavigateBack) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ScribbleFitTheme.colors.surface,
                    titleContentColor = ScribbleFitTheme.colors.primary
                )
            )
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = ScribbleFitTheme.colors.primary)
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error, color = ScribbleFitTheme.colors.dangerRed)
            }
        } else if (state.history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No history available.", 
                    style = ScribbleFitTheme.typography.bodyLarge, 
                    color = ScribbleFitTheme.colors.midGray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(ScribbleFitTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
            ) {
                state.groupedHistory.forEach { (monthYear, sessions) ->
                    stickyHeader {
                        Text(
                            text = monthYear,
                            style = ScribbleFitTheme.typography.labelSmall,
                            color = ScribbleFitTheme.colors.midGray,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(ScribbleFitTheme.colors.surface)
                                .padding(vertical = ScribbleFitTheme.spacing.small)
                        )
                    }
                    
                    items(sessions, key = { it.exercise.id }) { session ->
                        SessionRow(
                            session = session,
                            onClick = { onIntent(ExerciseHistoryIntent.NavigateToScribble(it.scribbleId, session.date)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionRow(
    session: ExerciseHistorySession,
    onClick: (ExerciseHistorySession) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
        color = ScribbleFitTheme.colors.surfaceContainerLowest,
        onClick = { onClick(session) }
    ) {
        Column(
            modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // To get Day and Date we need to format the session.date. 
                // For simplicity, let's just do a basic text.
                val instant = java.time.Instant.ofEpochMilli(session.date)
                val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
                val dateStr = localDate.format(DateTimeFormatter.ofPattern("EEE, MMM d"))

                Text(
                    text = dateStr,
                    style = ScribbleFitTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )

                if (session.isPersonalBest) {
                    Text(
                        text = "PB",
                        style = ScribbleFitTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700),
                        modifier = Modifier
                            .background(Color(0xFFFFD700).copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = session.summary,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.primary
            )

            Text(
                text = "${stringResource(R.string.exercise_details_volume)}: ${session.totalVolume.roundToInt()}",
                style = ScribbleFitTheme.typography.bodySmall,
                color = ScribbleFitTheme.colors.midGray
            )
        }
    }
}
