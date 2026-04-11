package com.scribblefit.core.database.entity.exercise

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a specific exercise (e.g., "Bench Press").
 */
@Entity(
    tableName = "exercise",
    indices = [Index(value = ["name"], unique = true)]
)
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0,
    val name: String,
    val muscleGroup: String,
    val isDraft: Boolean = false
)
