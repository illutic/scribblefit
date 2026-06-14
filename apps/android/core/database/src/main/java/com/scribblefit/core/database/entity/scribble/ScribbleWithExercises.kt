package com.scribblefit.core.database.entity.scribble

import androidx.room.Embedded
import androidx.room.Relation
import com.scribblefit.core.database.entity.exercise.ExerciseEntity
import com.scribblefit.core.database.entity.exercise.ExerciseWithSets
import com.scribblefit.core.database.entity.exercise.toDomain
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus

data class ScribbleWithExercises(
    @Embedded
    val scribble: ScribbleEntity,

    @Relation(
        entity = ExerciseEntity::class,
        parentColumn = "scribbleId",
        entityColumn = "scribbleId",
    )
    val exercises: List<ExerciseWithSets>
)

fun ScribbleWithExercises.toDomain(): Scribble =
    Scribble(
        id = scribble.scribbleId,
        rawText = scribble.rawText,
        status = runCatching { ScribbleStatus.valueOf(scribble.status.uppercase()) }.getOrDefault(
            ScribbleStatus.FAILED
        ),
        createdAt = scribble.createdAt,
        exercises = exercises.map { it.toDomain() }
    )