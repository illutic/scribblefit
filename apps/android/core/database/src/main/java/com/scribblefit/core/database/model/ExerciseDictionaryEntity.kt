package com.scribblefit.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Exercise_Dictionary")
data class ExerciseDictionaryEntity(
    @PrimaryKey
    val id: String,
    @ColumnInfo(name = "canonical_name")
    val canonicalName: String,
    @ColumnInfo(name = "muscle_group")
    val muscleGroup: String,
    val aliases: List<String>
)
