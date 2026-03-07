package com.scribblefit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.scribblefit.core.designsystem.theme.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasScreen
import com.scribblefit.feature.ledger.ui.LedgerScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val isInitialized by mainViewModel.isInitialized.collectAsState()

            ScribbleFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (isInitialized) {
                        MainScreen()
                    } else {
                        SplashScreen()
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val backStack = rememberNavBackStack(elements = arrayOf(Screen.Canvas as Screen))

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background,
                tonalElevation = 0.dp
            ) {
                val currentScreen = backStack.last()
                
                NavigationBarItem(
                    selected = currentScreen is Screen.Canvas,
                    onClick = { 
                        backStack.clear()
                        backStack.add(Screen.Canvas) 
                    },
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
                    onClick = { 
                        backStack.clear()
                        backStack.add(Screen.Analytics) 
                    },
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
                    onClick = { 
                        backStack.clear()
                        backStack.add(Screen.Exercises) 
                    },
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Exercises") },
                    label = { Text("Exercises") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                )
                NavigationBarItem(
                    selected = currentScreen is Screen.Profile,
                    onClick = { 
                        backStack.clear()
                        backStack.add(Screen.Profile) 
                    },
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
                    is Screen.Profile -> NavEntry(key) { ProfilePlaceholder() }
                    else -> error("Unknown screen: $key")
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
fun ProfilePlaceholder() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)
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
