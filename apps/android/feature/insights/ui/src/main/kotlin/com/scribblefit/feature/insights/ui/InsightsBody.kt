package com.scribblefit.feature.insights.ui

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.feature.insights.ui.components.AIOverviewSection
import com.scribblefit.feature.insights.ui.components.MuscleDistributionSection
import com.scribblefit.feature.insights.ui.components.StatsRow
import com.scribblefit.feature.insights.ui.components.VolumeChartSection

@Composable
internal fun InsightsBody(
    state: InsightsState,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> InsightsLoadingContent(state = state, modifier = modifier)
        state.isEmpty -> InsightsEmptyContent(state = state, modifier = modifier)
        else -> InsightsDataContent(state = state, modifier = modifier)
    }
}

@Composable
private fun InsightsLoadingContent(
    state: InsightsState,
    modifier: Modifier = Modifier,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "loadingAlpha"
    )

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = ScribbleFitTheme.spacing.screenPadding,
            end = ScribbleFitTheme.spacing.screenPadding,
            top = ScribbleFitTheme.spacing.screenPadding,
            bottom = 120.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
            ) {
                Text(
                    text = "\u23F3",
                    fontSize = 48.sp,
                    modifier = Modifier.alpha(alpha),
                )
                Text(
                    text = state.getLoadingText(),
                    style = ScribbleFitTheme.typography.titleMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = state.getLoadingSubtitle(),
                    style = ScribbleFitTheme.typography.bodyMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    textAlign = TextAlign.Center,
                )
            }
        }

        item { EmptySection(title = state.getThisWeek(), placeholder = state.getNothingToShow()) }
        item {
            EmptySection(
                title = state.getExercisesLabel(),
                placeholder = state.getNothingToShow()
            )
        }
    }
}

@Composable
private fun InsightsEmptyContent(
    state: InsightsState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = ScribbleFitTheme.spacing.screenPadding,
            end = ScribbleFitTheme.spacing.screenPadding,
            top = ScribbleFitTheme.spacing.screenPadding,
            bottom = 120.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.medium),
            ) {
                Text(
                    text = "\uD83C\uDF31",
                    fontSize = 48.sp,
                )
                Text(
                    text = state.getEmptyTitle(),
                    style = ScribbleFitTheme.typography.titleMedium,
                    color = ScribbleFitTheme.colors.primary,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = ScribbleFitTheme.spacing.medium),
                )
                Text(
                    text = state.getNoData(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                )
            }
        }

        item { EmptySection(title = state.getThisWeek(), placeholder = state.getNothingToShow()) }
        item {
            EmptySection(
                title = state.getExercisesLabel(),
                placeholder = state.getNothingToShow()
            )
        }
    }
}

@Composable
private fun EmptySection(
    title: String,
    placeholder: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.small)) {
        Text(
            text = title.uppercase(),
            style = ScribbleFitTheme.typography.labelMedium,
            color = ScribbleFitTheme.colors.midGray,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
        )
        Surface(
            color = ScribbleFitTheme.colors.surfaceContainerLow,
            shape = RoundedCornerShape(ScribbleFitTheme.shapes.medium),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = placeholder,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.midGray,
                modifier = Modifier.padding(ScribbleFitTheme.spacing.medium),
            )
        }
    }
}

@Composable
private fun InsightsDataContent(
    state: InsightsState,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(
            start = ScribbleFitTheme.spacing.screenPadding,
            end = ScribbleFitTheme.spacing.screenPadding,
            top = ScribbleFitTheme.spacing.screenPadding,
            bottom = 120.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitTheme.spacing.large),
    ) {
        if (state.insights != null || state.isGeneratingAI) {
            item {
                AIOverviewSection(state = state)
            }
        }

        item {
            StatsRow(state = state)
        }

        if (state.distribution.isNotEmpty()) {
            item {
                MuscleDistributionSection(state = state)
            }
        }

        if (state.volumePoints.isNotEmpty()) {
            item {
                VolumeChartSection(state = state)
            }
        }
    }
}
