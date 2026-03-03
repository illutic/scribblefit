package com.scribblefit.core.ai.data.mapper

import com.scribblefit.core.ai.model.ParsedExercise
import com.scribblefit.core.ai.model.ParsedSet
import com.scribblefit.core.ai.model.ParsedWorkout
import com.scribblefit.core.network.model.ParsedWorkoutDto

fun ParsedWorkoutDto.toDomain(): ParsedWorkout {
    return ParsedWorkout(
        date = date,
        location = location,
        exercises = exercises.map { it.toDomain() }
    )
}

private fun com.scribblefit.core.network.model.ParsedExerciseDto.toDomain(): ParsedExercise {
    return ParsedExercise(
        canonicalName = canonicalName,
        sets = sets.map { it.toDomain() }
    )
}

private fun com.scribblefit.core.network.model.ParsedSetDto.toDomain(): ParsedSet {
    return ParsedSet(
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes
    )
}
