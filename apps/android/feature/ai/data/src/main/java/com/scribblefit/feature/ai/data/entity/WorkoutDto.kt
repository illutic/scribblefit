package com.scribblefit.feature.ai.data.entity

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import com.scribblefit.core.model.Workout
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class WorkoutDto(
    val exercises: List<ExerciseDto>,
    val date: Long = System.currentTimeMillis(),
)

@Serializable
internal data class ExerciseDto(
    @SerialName("canonical_name")
    val canonicalName: String,
    @SerialName("muscle_group")
    val muscleGroup: String,
    val sets: List<SetDto>,
    @SerialName("estimated_1rm")
    val estimated1RM: Float? = null,
    val intensity: Float? = null,
    val improvement: Float? = null,
)

@Serializable
internal data class SetDto(
    val reps: Int,
    val setNumber: Int,
    val weight: Float? = null,
    val rpe: Float? = null,
    val notes: String? = null,
)

internal fun WorkoutDto.toDomain(): Workout =
    Workout(
        id = 0L, // ID will be assigned by the database
        date = date,
        exercises = exercises.map { it.toDomain() },
    )

internal fun ExerciseDto.toDomain(): Exercise =
    Exercise(
        id = 0L, // ID will be assigned by the database
        canonicalName = canonicalName,
        muscleGroup = muscleGroup,
        sets = sets.map { it.toDomain() },
        estimated1RM = estimated1RM,
        intensity = intensity,
        improvement = improvement,
    )

internal fun SetDto.toDomain(): Set =
    Set(
        id = 0L, // ID will be assigned by the database
        weight = weight,
        reps = reps,
        rpe = rpe,
        setNumber = setNumber,
        notes = notes,
    )
