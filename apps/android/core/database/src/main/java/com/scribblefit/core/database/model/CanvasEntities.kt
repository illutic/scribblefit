package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persists the state of the Intelligent Canvas feed to allow recovery after app kill.
 */
@Entity(tableName = "Canvas_Feed")
data class CanvasFeedEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "item_type")
    val type: String, // PROMPT, SCRIBBLE, CONFIRMATION, INSIGHT
    @ColumnInfo(name = "json_data")
    val jsonData: String, // Serialized FeedItem payload
    @ColumnInfo(name = "created_at")
    val createdAt: Long
)

/**
 * Persists the current uncommitted workout session.
 */
@Entity(tableName = "Active_Session")
data class ActiveSessionEntity(
    @PrimaryKey
    val id: String = "current_session",
    @ColumnInfo(name = "json_data")
    val jsonData: String, // Serialized WorkoutSession
    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)
