package com.scribblefit.core.database.entity.exercise

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.model.Exercise as DomainExercise

@Entity(
    tableName = "exercises",
    foreignKeys = [
        ForeignKey(
            entity = ScribbleEntity::class,
            parentColumns = ["scribbleId"],
            childColumns = ["scribbleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scribbleId"), Index("exerciseId")],
)
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val exerciseId: Long = 0,
    val scribbleId: Long,
    val name: String,
    val muscleGroup: String,
    val createdAt: Long,
    val isDraft: Boolean = false,
)

fun DomainExercise.toEntity(scribbleId: Long): ExerciseEntity =
    ExerciseEntity(
        exerciseId = id,
        name = canonicalName,
        muscleGroup = muscleGroup,
        isDraft = isDraft,
        scribbleId = scribbleId,
        createdAt = createdAt
    )
