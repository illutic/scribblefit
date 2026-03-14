package com.scribblefit.core.database.entity.workout

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a single workout session.
 */
@Entity(tableName = "workout")
data class Workout(
    @PrimaryKey(autoGenerate = true)
    val workoutId: Long = 0,
    val workoutDate: Long, // Unix timestamp
    val notes: String? = null,
    val isDraft: Boolean = false
)
