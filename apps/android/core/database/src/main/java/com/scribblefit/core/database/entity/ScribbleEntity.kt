package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Scribble",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exercise_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ]
)
data class ScribbleEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "status") val status: EntitySyncStatus,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "raw_text") val rawText: String = "",
    @ColumnInfo(name = "exercise_id") val exerciseId: String? = null
)

enum class EntitySyncStatus { PENDING, COMPLETED, FAILED }
