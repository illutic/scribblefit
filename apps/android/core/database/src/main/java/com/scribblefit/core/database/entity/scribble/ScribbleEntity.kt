package com.scribblefit.core.database.entity.scribble

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.scribblefit.core.model.Scribble

@Entity(tableName = "scribbles")
data class ScribbleEntity(
    @PrimaryKey(autoGenerate = true)
    val scribbleId: Long = 0,
    val rawText: String,
    val status: String,
    val parsedJson: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

fun Scribble.toEntity(): ScribbleEntity = ScribbleEntity(
    scribbleId = id,
    rawText = rawText,
    status = status.name,
    createdAt = createdAt
)