package com.scribblefit.feature.ledger.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LedgerScreen(
    viewModel: LedgerViewModel = hiltViewModel()
) {
    val history by viewModel.workoutHistory.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(80.dp))
        
        Text(
            text = "Ledger.",
            style = TextStyle(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF101010)
            )
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No history yet.",
                    style = TextStyle(color = Color.Gray, fontSize = 16.sp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(32.dp),
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(history) { workout ->
                    WorkoutItem(workout)
                }
            }
        }
    }
}

@Composable
fun WorkoutItem(workout: WorkoutHistory) {
    val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    val dateString = dateFormat.format(Date(workout.date))

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = dateString.uppercase(),
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )
            )
            
            Text(
                text = "${workout.totalVolume.toInt()} LBS",
                style = TextStyle(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF101010)
                )
            )
        }
        
        if (workout.location != null) {
            Text(
                text = workout.location,
                style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        workout.exercises.forEach { exercise ->
            ExerciseSection(exercise)
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        Divider(color = Color(0xFFF7F7F8), thickness = 1.dp, modifier = Modifier.padding(top = 16.dp))
    }
}

@Composable
fun ExerciseSection(exercise: com.scribblefit.feature.ledger.domain.model.ExerciseHistory) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = exercise.canonicalName,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF101010)
            )
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        exercise.sets.forEachIndexed { index, set ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "SET ${index + 1}:  ${set.weight.toInt()} x ${set.reps}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF101010),
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                )
                if (set.rpe != null) {
                    Text(
                        text = "  @ RPE ${set.rpe}",
                        style = TextStyle(fontSize = 12.sp, color = Color.Gray)
                    )
                }
            }
        }
    }
}
