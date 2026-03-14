package com.scribblefit.feature.ai.data.entity

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.Workout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WorkoutDto(
    val date: Long,
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
    val weight: Float,
    val reps: Int,
    val setNumber: Int,
    val rpe: Float? = null,
    val notes: String? = null
)

internal fun WorkoutDto.toDomain(): Workout = Workout(
    id = 0L, // ID will be assigned by the database
    date = date,
    exercises = exercises.map { it.toDomain() }
)

internal fun ExerciseDto.toDomain(): Exercise = Exercise(
    id = 0L, // ID will be assigned by the database
    canonicalName = canonicalName,
    muscleGroup = muscleGroup,
    sets = sets.map { it.toDomain() }
)

internal fun SetDto.toDomain(): Set = Set(
    id = 0L, // ID will be assigned by the database
    weight = weight,
    reps = reps,
    rpe = rpe,
    setNumber = setNumber,
    notes = notes
)