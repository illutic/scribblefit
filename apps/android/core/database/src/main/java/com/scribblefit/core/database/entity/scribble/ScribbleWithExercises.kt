package com.scribblefit.core.database.entity.scribble

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.exercise.WorkoutExerciseWithDetails

/**
 * Represents a scribble with all the exercises it generated.
 */
data class ScribbleWithExercises(
    @Embedded val scribble: ScribbleEntity,

    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "scribbleId",
        entityColumn = "workoutExerciseId",
        associateBy = Junction(ScribbleExercise::class)
    )
    val exercises: List<WorkoutExerciseWithDetails>
)
