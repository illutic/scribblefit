package com.scribblefit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.scribblefit.core.designsystem.ScribbleFitTheme
import com.scribblefit.core.navigation.Screen
import com.scribblefit.feature.canvas.ui.CanvasScreen
import com.scribblefit.feature.ledger.ui.LedgerScreen
import com.scribblefit.feature.profile.ui.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint

private val BottomNavItems = listOf(
    Triple(Screen.Canvas, "Home", Icons.Default.Home),
    Triple(Screen.Ledger, "Log", Icons.AutoMirrored.Filled.List),
    Triple(Screen.Settings, "Profile", Icons.Default.Person)
)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScribbleFitTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            BottomNavItems.forEach { (screen, label, icon) ->
                                NavigationBarItem(
                                    icon = { Icon(icon, contentDescription = label) },
                                    label = { Text(label) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(Screen.Canvas.route) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Canvas.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Canvas.route) { CanvasScreen() }
                        composable(Screen.Ledger.route) { LedgerScreen() }
                        composable(Screen.Settings.route) { ProfileScreen() }
                    }
                }
            }
        }
    }
}
