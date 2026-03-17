package com.scribblefit.feature.insights.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.insights.domain.model.MuscleGroupDistribution

@Composable
fun InsightsRoute(
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    InsightsScreen(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
internal fun InsightsScreen(
    state: InsightsState,
    onIntent: (InsightsIntent) -> Unit
) {
    Scaffold(
        topBar = { InsightsHeader(state) },
        bottomBar = { InsightsFooter(state, onIntent) },
        containerColor = ScribbleFitTheme.colors.background
    ) { padding ->
        InsightsBody(
            state = state,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
private fun InsightsHeader(state: InsightsState) {
    TopBar(title = state.getTitle())
}

@Composable
private fun InsightsBody(
    state: InsightsState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ScribbleFitTheme.colors.background),
        contentAlignment = Alignment.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(color = ScribbleFitTheme.colors.primary)
            }
            state.isEmpty -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = state.getEmptyTitle(),
                        style = ScribbleFitTheme.typography.h6,
                        color = ScribbleFitTheme.colors.onBackground
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.getEmptyDescription(),
                        style = ScribbleFitTheme.typography.body1,
                        color = ScribbleFitTheme.colors.onBackground.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
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
private fun FrequencySection(state: InsightsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = state.frequency?.totalWorkouts?.toString() ?: "0",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )
                Text(
                    text = state.getTotalWorkoutsLabel(),
                    style = ScribbleFitTheme.typography.caption
                )
            }
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .width(1.dp)
                    .background(Color.LightGray)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "%.1f".format(state.frequency?.workoutsPerWeek ?: 0f),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = ScribbleFitTheme.colors.primary
                )
                Text(
                    text = state.getWorkoutsPerWeekLabel(),
                    style = ScribbleFitTheme.typography.caption
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = state.getVolumeChartTitle(),
                style = ScribbleFitTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF7F7F8), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Volume Chart coming soon", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun MuscleDistributionSection(state: InsightsState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = state.getMuscleDistributionTitle(),
                style = ScribbleFitTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = item.muscleGroup, style = ScribbleFitTheme.typography.body2)
            Text(text = "${(item.percentage * 100).toInt()}%", style = ScribbleFitTheme.typography.body2)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFF0F0F0))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(item.percentage)
                    .fillMaxHeight()
                    .background(ScribbleFitTheme.colors.primary)
            )
        }
    }
}

@Composable
private fun InsightsFooter(
    state: InsightsState,
    onIntent: (InsightsIntent) -> Unit
) {
    BottomBarContainer(
        bottomBarState = state.bottomBarState,
        onClick = { screen -> onIntent(InsightsIntent.NavigateToScreen(screen)) }
    )
}
