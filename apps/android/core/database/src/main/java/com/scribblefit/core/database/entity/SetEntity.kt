package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class SetEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "workout_id", index = true) val workoutId: String,
    @ColumnInfo(name = "exercise_id", index = true) val exerciseId: String,
    @ColumnInfo(name = "weight") val weight: Double,
    @ColumnInfo(name = "reps") val reps: Int,
    @ColumnInfo(name = "rpe") val rpe: Double? = null,
    @ColumnInfo(name = "notes") val notes: String? = null
)
