package com.scribblefit.core.database.mapper

import com.scribblefit.core.config.domain.LLMProvider
import com.scribblefit.core.config.domain.ThemePreference
import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.database.entity.config.SystemConfig
import com.scribblefit.core.database.entity.exercise.Exercise
import com.scribblefit.core.database.entity.exercise.WorkoutExerciseWithDetails
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.scribble.ScribbleWithExercises
import com.scribblefit.core.database.entity.set.WorkoutSet
import com.scribblefit.core.database.entity.workout.Workout
import com.scribblefit.core.database.entity.workout.WorkoutWithAllDetails
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.config.domain.SystemConfig as DomainSystemConfig
import com.scribblefit.core.model.Exercise as DomainExercise
import com.scribblefit.core.model.Set as DomainSet
import com.scribblefit.core.model.Workout as DomainWorkout

/**
 * Mapper extension functions to convert Room entities into domain objects.
 */

fun Exercise.toDomain(): DomainExercise {
    return DomainExercise(
        id = exerciseId,
        canonicalName = name,
        muscleGroup = muscleGroup,
        sets = emptyList(),
        isDraft = isDraft
    )
}

fun Workout.toDomain(): DomainWorkout {
    return DomainWorkout(
        id = workoutId,
        date = workoutDate,
        exercises = emptyList(),
        notes = notes?.split("\n")
    )
}

fun WorkoutSet.toDomain(): DomainSet {
    return DomainSet(
        id = setId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes
    )
}

fun WorkoutExerciseWithDetails.toDomain(): DomainExercise {
    return DomainExercise(
        id = exercise.exerciseId,
        canonicalName = exercise.name,
        muscleGroup = exercise.muscleGroup,
        sets = sets.map { it.toDomain() },
        isDraft = exercise.isDraft
    )
}

fun WorkoutWithAllDetails.toDomain(): DomainWorkout {
    return DomainWorkout(
        id = workout.workoutId,
        date = workout.workoutDate,
        exercises = exercises.map { it.toDomain() },
        notes = workout.notes?.split("\n")
    )
}

fun SystemConfig.toDomain(): DomainSystemConfig {
    return DomainSystemConfig(
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        updatedAt = updatedAt,
        preferredModel = preferredModel,
        weightUnit = Weight.valueOf(weightUnit),
        preferredLlmProvider = LLMProvider.valueOf(preferredLlmProvider),
        themePreference = ThemePreference.valueOf(themePreference)
    )
}

fun ScribbleEntity.toDomain(): Scribble {
    return Scribble(
        id = scribbleId,
        rawText = rawText,
        parsedJson = parsedJson,
        status = ScribbleStatus.valueOf(status),
        createdAt = createdAt
    )
}

fun ScribbleWithExercises.toDomain(): Scribble {
    return Scribble(
        id = scribble.scribbleId,
        rawText = scribble.rawText,
        parsedJson = scribble.parsedJson,
        status = ScribbleStatus.valueOf(scribble.status),
        createdAt = scribble.createdAt,
        exercises = exercises.map { it.toDomain() }
    )
}

/**
 * Mapper extension functions to convert domain objects into Room entities.
 */

fun Scribble.toEntity(): ScribbleEntity {
    return ScribbleEntity(
        scribbleId = id,
        rawText = rawText,
        parsedJson = parsedJson,
        status = status.name,
        createdAt = createdAt
    )
}

fun DomainSet.toEntity(workoutExerciseId: Long): WorkoutSet {
    return WorkoutSet(
        setId = id,
        workoutExerciseId = workoutExerciseId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes
    )
}

fun DomainExercise.toEntity(): Exercise {
    return Exercise(
        exerciseId = id,
        name = canonicalName,
        muscleGroup = muscleGroup,
        isDraft = isDraft
    )
}

fun DomainWorkout.toEntity(): Workout {
    return Workout(
        workoutId = id,
        workoutDate = date,
        notes = notes?.joinToString("\n")
    )
}

fun DomainSystemConfig.toEntity(): SystemConfig {
    return SystemConfig(
        id = 0, // Single row with fixed key
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        preferredLlmProvider = preferredLlmProvider.name,
        updatedAt = updatedAt,
        preferredModel = preferredModel,
        weightUnit = weightUnit.name,
        themePreference = themePreference.name
    )
}