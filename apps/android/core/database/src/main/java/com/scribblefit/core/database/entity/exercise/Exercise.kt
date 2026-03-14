package com.scribblefit.core.database.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a specific exercise (e.g., "Bench Press").
 */
@Entity(tableName = "exercise")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0,
    val name: String,
    val muscleGroup: String
)
