package com.scribblefit.core.database.entity.exercise

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.workout.Workout

/**
 * Bridge table linking Workouts and Exercises.
 * Represents a specific exercise performed within a workout.
 */
@Entity(
    tableName = "workout_exercise",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["workoutId"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["workoutId"]),
        Index(value = ["exerciseId"])
    ]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true)
    val workoutExerciseId: Long = 0,
    val workoutId: Long? = null,
    val exerciseId: Long,
    val estimated1RM: Float? = null,
    val intensity: Float? = null,
    val improvement: Float? = null,
)
