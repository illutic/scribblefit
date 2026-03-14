package com.scribblefit.core.database.entity.scribble

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.exercise.WorkoutExercise

/**
 * Stores unstructured text input ("scribbles") and their parsed state.
 */
@Entity(
    tableName = "scribbles",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["workoutExerciseId"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["workoutExerciseId"])
    ]
)
data class ScribbleEntity(
    @PrimaryKey(autoGenerate = true)
    val scribbleId: Long = 0,
    val rawText: String,
    val parsedJson: String? = null,
    val status: String, // Maps to ScribbleStatus enum
    val workoutExerciseId: Long? = null, // Populated ONLY when status is COMPLETED
    val createdAt: Long = System.currentTimeMillis()
)
