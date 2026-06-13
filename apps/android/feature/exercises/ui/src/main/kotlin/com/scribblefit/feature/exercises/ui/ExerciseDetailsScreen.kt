package com.scribblefit.feature.exercises.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.exercises.ui.components.ExerciseDetailsHeader
import com.scribblefit.feature.exercises.ui.components.ExerciseInsightCard
import com.scribblefit.feature.exercises.ui.components.HistorySection
import com.scribblefit.feature.exercises.ui.components.TrendsSection
import com.scribblefit.feature.exercises.ui.components.WeeklyStatsCard

@Composable
fun ExerciseDetailsScreen(
    viewModel: ExerciseDetailsViewModel
) {
    val state by viewModel.state.collectAsState()

    ExerciseDetailsContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
private fun ExerciseDetailsContent(
    state: ExerciseDetailsState,
    onIntent: (ExerciseDetailsIntent) -> Unit
) {
    Scaffold(
        topBar = {
            ExerciseDetailsHeader(
                exerciseName = state.exerciseName,
                onBackClick = { onIntent(ExerciseDetailsIntent.NavigateBack) }
            )
        },
        containerColor = ScribbleFitTheme.colors.surface
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScribbleFitTheme.spacing.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp),
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.extraLarge)
            ) {
                if (state.isGeneratingAI || state.aiInsight != null) {
                    ExerciseInsightCard(
                        insight = state.aiInsight,
                        isGenerating = state.isGeneratingAI,
                        recommendationLabel = state.recommendationLabel
                    )
                }

                state.weeklyStats?.let { stats ->
                    WeeklyStatsCard(
                        stats = stats,
                        weightUnit = state.weightUnitLabel,
                        titleLabel = state.weeklyPerformanceLabel,
                        activityLabel = state.activityLabel,
                        sessionsUnitLabel = state.sessionsUnitLabel,
                        volumeLabel = state.volumeLabel,
                        maxWeightLabel = state.maxWeightLabel
                    )
                }

                state.trends?.let { trends ->
                    TrendsSection(
                        trends = trends,
                        weightUnit = state.weightUnitLabel,
                        titleLabel = state.trendsLabel,
                        viewAllLabel = state.viewAllLabel,
                        current1rmLabel = state.current1rmLabel,
                        intensityLabel = state.intensityLabel,
                        weightVsLastLabel = state.weightVsLastLabel,
                        lastVolumeLabel = state.lastVolumeLabel,
                        onViewAllClick = { onIntent(ExerciseDetailsIntent.NavigateToTrends) },
                        getTrendDirectionText = { state.getTrendDirectionText(it) }
                    )
                }

                state.weeklyStats?.let { stats ->
                    HistorySection(
                        historyCount = stats.sessions,
                        titleLabel = state.historyLabel,
                        viewAllSessionsText = state.viewAllSessionsText,
                        totalSessionsText = state.getTotalSessionsText(stats.sessions),
                        onViewHistoryClick = { /* TODO */ }
                    )
                }

                Spacer(modifier = Modifier.height(ScribbleFitTheme.spacing.large))
            }
        }
    }
}
