package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.core.designsystem.ScribbleFitColors
import com.scribblefit.core.designsystem.ScribbleFitSpacing

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(ScribbleFitSpacing.screenPadding),
        verticalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Medium)
    ) {
        Text(text = uiState.userName, color = ScribbleFitColors.RichBlack, fontSize = 28.sp)
        uiState.stats?.let { stats ->
            Text(text = "${stats.totalWorkouts} workouts", color = ScribbleFitColors.MidGray, fontSize = 16.sp)
            Text(text = "${stats.lifetimeVolume.toInt()} lbs total volume", color = ScribbleFitColors.MidGray, fontSize = 16.sp)
        }
    }
}
