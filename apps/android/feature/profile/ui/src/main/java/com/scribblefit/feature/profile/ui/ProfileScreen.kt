package com.scribblefit.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitShapes
import com.scribblefit.core.designsystem.theme.tokens.ScribbleFitSpacing
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = ScribbleFitSpacing.ScreenPadding)
    ) {
        Spacer(modifier = Modifier.height(ScribbleFitSpacing.XL))

        // Profile Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = uiState.userName.take(1).uppercase(),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.width(ScribbleFitSpacing.Medium))
            Column {
                Text(
                    text = uiState.userName,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Member since ${formatDate(uiState.stats?.joinDate ?: System.currentTimeMillis())}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(ScribbleFitSpacing.XL))

        // Stats Grid
        Text(
            text = "LIFETIME STATS",
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(ScribbleFitSpacing.Medium))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
            verticalArrangement = Arrangement.spacedBy(ScribbleFitSpacing.Small),
            modifier = Modifier.height(200.dp)
        ) {
            item { StatCard("Total Workouts", (uiState.stats?.totalWorkouts ?: 0).toString()) }
            item { StatCard("Total Volume", formatVolume(uiState.stats?.lifetimeVolume ?: 0.0)) }
            item { StatCard("PRs Hit", (uiState.stats?.prCount ?: 0).toString()) }
            item { StatCard("Current Streak", "3 days") }
        }

        Spacer(modifier = Modifier.height(ScribbleFitSpacing.XL))

        // Navigation Actions
        SettingsRow("App Settings", Icons.Default.Settings, onClick = viewModel::onSettingsClick)
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Surface(
        shape = ScribbleFitShapes.Large,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = value, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun SettingsRow(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onBackground)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatVolume(volume: Double): String {
    return if (volume >= 1000) {
        "${(volume / 1000).toInt()}k lbs"
    } else {
        "${volume.toInt()} lbs"
    }
}
