package com.scribblefit.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SettingsScreen(
        uiState = uiState,
        onProviderChanged = viewModel::onProviderChanged,
        onModelSelected = viewModel::onModelSelected,
        onApiKeySaved = viewModel::onApiKeySaved,
        onWeightUnitChanged = viewModel::onWeightUnitChanged,
        onThemeChanged = viewModel::onThemeChanged,
        onClearDataTapped = viewModel::onClearDataTapped,
        modifier = modifier
    )
}
