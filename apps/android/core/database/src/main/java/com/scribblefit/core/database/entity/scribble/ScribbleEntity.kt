package com.scribblefit.core.database.entity.scribble

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores unstructured text input ("scribbles") and their parsed state.
 */
@Entity(tableName = "scribbles")
data class ScribbleEntity(
    @PrimaryKey(autoGenerate = true)
    val scribbleId: Long = 0,
    val rawText: String,
    val parsedJson: String? = null,
    val status: String, // Maps to ScribbleStatus enum
    val createdAt: Long = System.currentTimeMillis(),
    val workoutId: Long? = null
)
