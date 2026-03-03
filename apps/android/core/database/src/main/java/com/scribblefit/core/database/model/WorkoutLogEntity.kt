package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Workout_Logs")
data class WorkoutLogEntity(
    @PrimaryKey
    val id: String,
    val date: Long,
    val location: String?,
    @ColumnInfo(name = "total_volume")
    val totalVolume: Double?
)
