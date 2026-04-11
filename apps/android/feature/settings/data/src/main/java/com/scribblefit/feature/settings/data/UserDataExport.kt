package com.scribblefit.feature.settings.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDataExport(
    val scribbles: List<ScribbleExport>,
    val workouts: List<WorkoutExport>
)

@Serializable
data class ScribbleExport(
    val id: Long,
    val rawText: String,
    val status: String,
    val createdAt: Long,
    val exercises: List<ScribbleExerciseExport>
)

@Serializable
data class ScribbleExerciseExport(
    val exerciseName: String,
    val muscleGroup: String
)

@Serializable
data class WorkoutExport(
    val id: Long,
    val date: Long,
    val notes: String?,
    val exercises: List<WorkoutExerciseExport>
)

@Serializable
data class WorkoutExerciseExport(
    val exerciseName: String,
    val muscleGroup: String,
    val sets: List<WorkoutSetExport>
)

@Serializable
data class WorkoutSetExport(
    val setNumber: Int,
    val reps: Int,
    val weight: Float,
    val rpe: Float?,
    val notes: String?
)
