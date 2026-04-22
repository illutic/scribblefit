package com.scribblefit.feature.exercises.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.components.TrainingExerciseCard
import com.scribblefit.feature.exercises.ui.components.WorkoutExercisesHeader

@Composable
internal fun WorkoutExercisesScreen(
    state: WorkoutExercisesState,
    onIntent: (WorkoutExercisesIntent) -> Unit,
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(ScribbleFitTheme.spacing.medium),
                contentAlignment = Alignment.TopCenter
            ) {
                WorkoutExercisesHeader(
                    dateString = state.dateString,
                    totalExercises = state.totalExercises,
                    totalSets = state.totalSets,
                    totalVolume = state.totalVolume,
                    exercisesLabel = state.exercisesLabel,
                    setsLabel = state.setsLabel,
                    volumeLabel = state.volumeLabel,
                    backContentDescription = state.backContentDescription,
                    onBackClick = { onIntent(WorkoutExercisesIntent.NavigateBack) },
                    modifier = Modifier.widthIn(max = 600.dp)
                )
            }
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.TopCenter
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = ScribbleFitTheme.colors.primary)
                }
            } else if (state.error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error,
                        style = ScribbleFitTheme.typography.bodyLarge,
                        color = ScribbleFitTheme.colors.dangerRed
                    )
                }
            } else {
                val uiModels = state.uiModels
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = ScribbleFitTheme.spacing.large,
                        vertical = ScribbleFitTheme.spacing.medium
                    ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium)
                ) {
                    items(uiModels, key = { it.id }) { exercise ->
                        TrainingExerciseCard(
                            name = exercise.name,
                            formattedSummary = exercise.formattedSummary,
                            estimated1RM = state.getEstimated1RM(exercise),
                            intensity = state.getIntensity(exercise),
                            improvement = state.getImprovement(exercise),
                            hasStats = exercise.hasStats,
                            onClick = { onIntent(WorkoutExercisesIntent.ExerciseClicked(exercise.name)) },
                            modifier = Modifier.widthIn(max = 600.dp),
                            fontSize = 28,
                            kerning = -1.0
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.large))
                    }
                }
            }
        }
    }
}
