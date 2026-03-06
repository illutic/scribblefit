package com.scribblefit.feature.canvas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CanvasScreen(
    viewModel: CanvasViewModel = hiltViewModel()
) {
    val text by viewModel.scribbleText.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        Text(
            text = "Scribble.",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101010)
            ),
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (text.isEmpty()) {
                Text(
                    text = "Bench 135x5, 135x5...",
                    style = TextStyle(
                        fontSize = 20.sp,
                        color = Color.LightGray
                    )
                )
            }
            
            BasicTextField(
                value = text,
                onValueChange = viewModel::onTextChange,
                textStyle = TextStyle(
                    fontSize = 20.sp,
                    color = Color(0xFF101010),
                    lineHeight = 28.sp
                ),
                modifier = Modifier.fillMaxSize()
            )
        }

        Button(
            onClick = viewModel::submitScribble,
            enabled = text.isNotBlank() && !isSyncing,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF101010),
                contentColor = Color.White,
                disabledContainerColor = Color.LightGray
            ),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (isSyncing) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Log Workout",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
