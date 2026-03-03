package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "Sets",
    foreignKeys = [
        ForeignKey(
            entity = WorkoutLogEntity::class,
            parentColumns = ["id"],
            childColumns = ["workout_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseDictionaryEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["workout_id"]),
        Index(value = ["exercise_id"])
    ]
)
data class SetEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "workout_id")
    val workoutId: String,
    @ColumnInfo(name = "exercise_id")
    val exerciseId: String,
    val weight: Double,
    val reps: Int,
    val rpe: Double?,
    val notes: String?
)
