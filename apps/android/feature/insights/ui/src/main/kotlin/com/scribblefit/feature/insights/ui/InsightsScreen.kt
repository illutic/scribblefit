package com.scribblefit.feature.insights.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.core.designsystem.BottomBarContainer
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.designsystem.TopBar
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution

@Composable
fun InsightsRoute(viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()

    InsightsScreen(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun InsightsScreen(
    state: InsightsState,
    onIntent: (InsightsIntent) -> Unit,
) {
    Scaffold(
        topBar = { InsightsHeader(state) },
        bottomBar = { InsightsFooter(state, onIntent) },
        containerColor = ScribbleFitTheme.colors.background,
    ) { padding ->
        InsightsBody(
            state = state,
            modifier = Modifier.padding(padding),
        )
    }
}

@Composable
private fun InsightsHeader(state: InsightsState) {
    TopBar(title = { Text(state.getTitle()) })
}

@Composable
private fun InsightsBody(
    state: InsightsState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(ScribbleFitTheme.colors.background),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(color = ScribbleFitTheme.colors.richBlack)
            }

            state.isEmpty -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp),
                ) {
                    Text(
                        text = state.getEmptyTitle(),
                        style = ScribbleFitTheme.typography.titleLarge,
                        color = ScribbleFitTheme.colors.richBlack,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.getEmptyDescription(),
                        style = ScribbleFitTheme.typography.bodyLarge,
                        color = ScribbleFitTheme.colors.midGray,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                ) {
                    item {
                        AIOverviewSection(state)
                    }
                    item {
                        FrequencySection(state)
                    }
                    item {
                        VolumeSection(state)
                    }
                    item {
                        MuscleDistributionSection(state)
                    }
                }
            }
        }
    }
}

@Composable
private fun AIOverviewSection(state: InsightsState) {
    Column {
        Text(
            text = state.getAIOverviewTitle(),
            style = ScribbleFitTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        if (state.isGeneratingAI) {
            AIOverviewLoading(state)
        } else {
            state.aiOverview?.let { overview ->
                AIOverviewCard(overview)
            }
        }
    }
}

@Composable
private fun AIOverviewLoading(state: InsightsState) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = ScribbleFitTheme.colors.richBlack
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.getAIOverviewGeneratingText(),
                    style = ScribbleFitTheme.typography.labelMedium,
                    color = ScribbleFitTheme.colors.midGray
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ScribbleFitTheme.colors.softGray.copy(alpha = alpha))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ScribbleFitTheme.colors.softGray.copy(alpha = alpha))
            )
        }
    }
}

@Composable
private fun AIOverviewCard(overview: com.scribblefit.feature.insights.domain.model.AIOverview) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = overview.summary,
                style = ScribbleFitTheme.typography.bodyLarge,
                color = ScribbleFitTheme.colors.richBlack
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = ScribbleFitTheme.colors.softGray)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Trends",
                style = ScribbleFitTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.midGray
            )
            Text(
                text = overview.trends,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.strongGray
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Advice",
                style = ScribbleFitTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = ScribbleFitTheme.colors.midGray
            )
            Text(
                text = overview.advice,
                style = ScribbleFitTheme.typography.bodyMedium,
                color = ScribbleFitTheme.colors.strongGray
            )
        }
    }
}

@Composable
private fun FrequencySection(state: InsightsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.frequency?.totalWorkouts?.toString() ?: "0",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.richBlack,
                )
                Text(
                    text = state.getTotalWorkoutsLabel(),
                    style = ScribbleFitTheme.typography.labelSmall,
                )
            }
            Box(
                modifier =
                    Modifier
                        .height(40.dp)
                        .width(1.dp)
                        .background(ScribbleFitTheme.colors.lightGray),
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%.1f".format(state.frequency?.workoutsPerWeek ?: 0f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.richBlack,
                )
                Text(
                    text = state.getWorkoutsPerWeekLabel(),
                    style = ScribbleFitTheme.typography.labelSmall,
                )
            }
        }
    }
}

@Composable
private fun VolumeSection(state: InsightsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = state.getVolumeChartTitle(),
                style = ScribbleFitTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(ScribbleFitTheme.colors.softGray, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text("Volume Chart coming soon", color = ScribbleFitTheme.colors.midGray)
            }
        }
    }
}

@Composable
private fun MuscleDistributionSection(state: InsightsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = state.getMuscleDistributionTitle(),
                style = ScribbleFitTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(16.dp))
            state.distribution.take(5).forEach { item ->
                MuscleGroupItem(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun MuscleGroupItem(item: MuscleGroupDistribution) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = item.muscleGroup, style = ScribbleFitTheme.typography.bodyMedium)
            Text(text = "${(item.percentage * 100).toInt()}%", style = ScribbleFitTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(ScribbleFitTheme.colors.softGray),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth(item.percentage)
                        .fillMaxHeight()
                        .background(ScribbleFitTheme.colors.richBlack),
            )
        }
    }
}

@Composable
private fun InsightsFooter(
    state: InsightsState,
    onIntent: (InsightsIntent) -> Unit,
) {
    BottomBarContainer(
        bottomBarState = state.bottomBarState,
        onClick = { screen -> onIntent(InsightsIntent.NavigateToScreen(screen)) },
    )
}
