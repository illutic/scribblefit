package com.scribblefit.feature.ai.data.mapper

import com.scribblefit.core.network.model.ParsedExerciseDto
import com.scribblefit.core.network.model.ParsedSetDto
import com.scribblefit.core.network.model.ParsedWorkoutDto
import com.scribblefit.feature.ai.domain.model.ParsedExercise
import com.scribblefit.feature.ai.domain.model.ParsedSet
import com.scribblefit.feature.ai.domain.model.ParsedWorkout

fun ParsedWorkoutDto.toDomain(): ParsedWorkout {
    return ParsedWorkout(
        date = date,
        location = location,
        exercises = exercises.map { it.toDomain() }
    )
}

private fun ParsedExerciseDto.toDomain(): ParsedExercise {
    return ParsedExercise(
        canonicalName = canonicalName,
        sets = sets.map { it.toDomain() }
    )
}

private fun ParsedSetDto.toDomain(): ParsedSet {
    return ParsedSet(
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes
    )
}
