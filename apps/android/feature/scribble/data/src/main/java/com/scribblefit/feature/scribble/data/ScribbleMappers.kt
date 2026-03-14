package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.model.Scribble

/**
 * Maps ScribbleEntity to the domain Scribble model.
 */
fun ScribbleEntity.toDomain(): Scribble {
    return Scribble(
        id = scribbleId,
        rawText = rawText,
        parsedJson = parsedJson,
        status = status,
        workoutExerciseId = workoutExerciseId,
        createdAt = createdAt
    )
}

/**
 * Maps domain Scribble model to ScribbleEntity.
 */
fun Scribble.toEntity(): ScribbleEntity {
    return ScribbleEntity(
        scribbleId = id,
        rawText = rawText,
        parsedJson = parsedJson,
        status = status,
        workoutExerciseId = workoutExerciseId,
        createdAt = createdAt
    )
}
