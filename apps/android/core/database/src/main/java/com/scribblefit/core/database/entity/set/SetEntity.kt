package com.scribblefit.core.database.entity.set

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.exercise.ExerciseEntity
import com.scribblefit.core.model.Set

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["exerciseId"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("exerciseId"), Index("setId")]
)
data class SetEntity(
    @PrimaryKey(autoGenerate = true)
    val setId: Long = 0,
    val exerciseId: Long,
    val setNumber: Int,
    val reps: Int,
    val weight: Float? = null,
    val rpe: Float? = null,
    val notes: String? = null
)

fun SetEntity.toDomain(): Set = Set(
    id = setId,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rpe = rpe,
    notes = notes
)

fun Set.toEntity(exerciseId: Long): SetEntity = SetEntity(
    setId = id,
    exerciseId = exerciseId,
    setNumber = setNumber,
    weight = weight,
    reps = reps,
    rpe = rpe,
    notes = notes
)