package com.scribblefit.core.database.entity.scribble

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.exercise.WorkoutExercise

@Entity(
    tableName = "scribble_exercise",
    foreignKeys = [
        ForeignKey(
            entity = ScribbleEntity::class,
            parentColumns = ["scribbleId"],
            childColumns = ["scribbleId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = WorkoutExercise::class,
            parentColumns = ["workoutExerciseId"],
            childColumns = ["workoutExerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["scribbleId"]),
        Index(value = ["workoutExerciseId"])
    ]
)
data class ScribbleExercise(
    @PrimaryKey(autoGenerate = true)
    val scribbleExerciseId: Long = 0,
    val scribbleId: Long,
    val workoutExerciseId: Long,
)
