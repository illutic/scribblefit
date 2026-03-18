package com.scribblefit.core.database.entity.set

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.exercise.WorkoutExercise

/**
 * Represents a single set performed for a WorkoutExercise.
 */
@Entity(
    tableName = "workout_set",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["workoutExerciseId"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["workoutExerciseId"]),
    ],
)
data class WorkoutSet(
    @PrimaryKey(autoGenerate = true)
    val setId: Long = 0,
    val workoutExerciseId: Long,
    val setNumber: Int,
    val reps: Int,
    val weight: Float,
    val rpe: Float? = null,
    val notes: String? = null,
)
