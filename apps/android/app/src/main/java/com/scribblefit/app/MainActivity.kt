package com.scribblefit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.scribblefit.core.designsystem.theme.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasScreen
import com.scribblefit.feature.ledger.ui.LedgerScreen
import com.scribblefit.feature.profile.ui.ProfileScreen
import com.scribblefit.feature.profile.ui.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isInitialized by mainViewModel.isInitialized.collectAsState()

            ScribbleFitTheme {
                if (isInitialized) {
                    MainScreen(
                        backStack = mainViewModel.backStack,
                        onNavigateTo = mainViewModel::navigateTo,
                        onTabNavigate = mainViewModel::navigateTab,
                        onBack = mainViewModel::goBack
                    )
                } else {
                    SplashScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    backStack: NavBackStack<Screen>,
    onNavigateTo: (Screen) -> Unit,
    onTabNavigate: (Screen) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                val currentScreen = backStack.last()

                NavigationBarItem(
                    selected = currentScreen is Screen.Canvas,
                    onClick = { onTabNavigate(Screen.Canvas) },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Workout") },
                    label = { Text("Workout") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentScreen is Screen.Analytics,
                    onClick = { onTabNavigate(Screen.Analytics) },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "Analytics") },
                    label = { Text("Analytics") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentScreen is Screen.Exercises,
                    onClick = { onTabNavigate(Screen.Exercises) },
                    icon = {
                        Icon(
                            Icons.AutoMirrored.Filled.List,
                            contentDescription = "Exercises"
                        )
                    },
                    label = { Text("Exercises") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentScreen is Screen.Profile || currentScreen is Screen.Settings,
                    onClick = { onTabNavigate(Screen.Profile) },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(padding),
            entryProvider = { key ->
                when (key) {
                    is Screen.Canvas -> NavEntry(key) { CanvasScreen() }
                    is Screen.Analytics -> NavEntry(key) { AnalyticsPlaceholder() }
                    is Screen.Exercises -> NavEntry(key) { LedgerScreen() }
                    is Screen.Profile -> NavEntry(key) { ProfileScreen() }
                    is Screen.Settings -> NavEntry(key) { SettingsScreen() }
                }
            }
        )
    }
}

@Composable
fun AnalyticsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Analytics Dashboard", style = MaterialTheme.typography.headlineMedium)
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ScribbleFit",
                style = MaterialTheme.typography.headlineLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(100.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}
