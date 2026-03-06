package com.scribblefit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
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
    var currentScreen by remember { mutableStateOf(Screen.Canvas) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == Screen.Canvas,
                    onClick = { currentScreen = Screen.Canvas },
                    icon = { Icon(Icons.Default.Create, contentDescription = "Canvas") },
                    label = { Text("Canvas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF101010),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF101010),
                        indicatorColor = Color(0xFFF7F7F8)
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == Screen.Ledger,
                    onClick = { currentScreen = Screen.Ledger },
                    icon = { Icon(Icons.Default.List, contentDescription = "Ledger") },
                    label = { Text("Ledger") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF101010),
                        unselectedIconColor = Color.Gray,
                        selectedTextColor = Color(0xFF101010),
                        indicatorColor = Color(0xFFF7F7F8)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (currentScreen) {
                Screen.Canvas -> CanvasScreen()
                Screen.Ledger -> LedgerScreen()
            }
        }
    }
}

enum class Screen {
    Canvas, Ledger
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "ScribbleFit",
                style = TextStyle(
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF101010),
                    letterSpacing = (-2).sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(
                modifier = Modifier.width(100.dp),
                color = Color(0xFF101010),
                trackColor = Color(0xFFF7F7F8)
            )
        }
    }
}
