package com.scribblefit.core.database.entity.exercise

import androidx.room.Embedded
import androidx.room.Relation
import com.scribblefit.core.database.entity.set.WorkoutSet

/**
 * Groups a WorkoutExercise with its Exercise metadata and all performed sets.
 */
data class WorkoutExerciseWithDetails(
    @Embedded val workoutExercise: WorkoutExercise,

    @Relation(
        parentColumn = "exerciseId",
        entityColumn = "exerciseId"
    )
    val exercise: Exercise,

    @Relation(
        parentColumn = "workoutExerciseId",
        entityColumn = "workoutExerciseId"
    )
    val sets: List<WorkoutSet>
)
