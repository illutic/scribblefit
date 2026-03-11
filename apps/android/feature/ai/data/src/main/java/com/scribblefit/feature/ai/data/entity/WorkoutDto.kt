package com.scribblefit.feature.ai.data.entity

import com.scribblefit.feature.workout.domain.Exercise
import com.scribblefit.feature.workout.domain.Workout
import com.scribblefit.feature.workout.domain.Set
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class WorkoutDto(
    val date: String,
    val exercises: List<ExerciseDto>
)

@Serializable
internal data class ExerciseDto(
    @SerialName("canonical_name")
    val canonicalName: String,
    @SerialName("muscle_group")
    val muscleGroup: String,
    val sets: List<SetDto>
)

@Serializable
internal data class SetDto(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)

internal fun WorkoutDto.toDomain(): Workout = Workout(
    date = date,
    exercises = exercises.map { it.toDomain() }
)

internal fun ExerciseDto.toDomain(): Exercise = Exercise(
    canonicalName = canonicalName,
    muscleGroup = muscleGroup,
    sets = sets.map { it.toDomain() }
)

internal fun SetDto.toDomain(): Set = Set(
    weight = weight,
    reps = reps,
    rpe = rpe,
    notes = notes
)