package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Workout_Logs")
data class WorkoutLogEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "location") val location: String? = null,
    @ColumnInfo(name = "total_volume") val totalVolume: Double? = null
)
