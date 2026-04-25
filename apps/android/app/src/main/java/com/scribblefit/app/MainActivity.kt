package com.scribblefit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.scribblefit.app.navigation.MainNavigation
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.designsystem.ScribbleFitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appState by viewModel.appState.collectAsStateWithLifecycle()
            ScribbleFitTheme(
                isDynamicTheme = appState.isDynamicTheme,
                isSystemInDarkTheme =
                    when (appState.themePreference) {
                        ThemePreference.SYSTEM -> isSystemInDarkTheme()
                        ThemePreference.LIGHT -> false
                        ThemePreference.DARK -> true
                    },
            ) {
                MainNavigation(
                    navState = appState.navState,
                    onBack = { viewModel.onIntent(AppIntent.NavigateBack) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
