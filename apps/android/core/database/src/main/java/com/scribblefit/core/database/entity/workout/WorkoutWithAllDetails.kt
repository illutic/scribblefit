package com.scribblefit.core.database.entity.workout

import androidx.room.Embedded
import androidx.room.Relation
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.exercise.WorkoutExerciseWithDetails

/**
 * The complete aggregate for a Workout, including all exercises and their sets.
 */
data class WorkoutWithAllDetails(
    @Embedded val workout: Workout,

    @Relation(
        entity = WorkoutExercise::class,
        parentColumn = "workoutId",
        entityColumn = "workoutId"
    )
    val exercises: List<WorkoutExerciseWithDetails>
)
