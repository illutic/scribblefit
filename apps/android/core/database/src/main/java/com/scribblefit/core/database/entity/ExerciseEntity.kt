package com.scribblefit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.scribblefit.core.database.converter.StringListConverter

@Entity(tableName = "Exercise")
@TypeConverters(StringListConverter::class)
data class ExerciseEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "canonical_name") val canonicalName: String,
    @ColumnInfo(name = "muscle_group") val muscleGroup: String = "",
    @ColumnInfo(name = "aliases") val aliases: List<String> = emptyList()
)
