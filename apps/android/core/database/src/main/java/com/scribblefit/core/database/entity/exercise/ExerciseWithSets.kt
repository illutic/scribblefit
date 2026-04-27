package com.scribblefit.core.database.entity.exercise

import androidx.room.Embedded
import androidx.room.Relation
import com.scribblefit.core.database.entity.set.SetEntity
import com.scribblefit.core.database.entity.set.toDomain
import com.scribblefit.core.model.Exercise

data class ExerciseWithSets(
    @Embedded
    val exercise: ExerciseEntity,

    @Relation(
        entity = SetEntity::class,
        parentColumn = "exerciseId",
        entityColumn = "exerciseId",
    )
    val sets: List<SetEntity>
)

fun ExerciseWithSets.toDomain(): Exercise = Exercise(
    id = exercise.exerciseId,
    scribbleId = exercise.scribbleId,
    canonicalName = exercise.name,
    muscleGroup = exercise.muscleGroup,
    sets = sets.map { it.toDomain() },
    isDraft = exercise.isDraft,
    createdAt = exercise.createdAt
)