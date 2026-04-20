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
import org.json.JSONObject
import com.scribblefit.core.config.domain.SystemConfig as DomainSystemConfig
import com.scribblefit.core.model.Exercise as DomainExercise
import com.scribblefit.core.model.Set as DomainSet
import com.scribblefit.core.model.Workout as DomainWorkout

/**
 * Mapper extension functions to convert Room entities into domain objects.
 */

fun Exercise.toDomain(): DomainExercise =
    DomainExercise(
        id = exerciseId,
        canonicalName = name,
        muscleGroup = muscleGroup,
        sets = emptyList(),
        isDraft = isDraft,
    )

fun Workout.toDomain(): DomainWorkout =
    DomainWorkout(
        id = workoutId,
        date = workoutDate,
        exercises = emptyList(),
        notes = notes?.split("\n"),
    )

fun WorkoutSet.toDomain(): DomainSet =
    DomainSet(
        id = setId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes,
    )

fun WorkoutExerciseWithDetails.toDomain(): DomainExercise =
    DomainExercise(
        id = workoutExercise.workoutExerciseId,
        canonicalName = exercise.name,
        muscleGroup = exercise.muscleGroup,
        sets = sets.map { it.toDomain() },
        isDraft = exercise.isDraft,
        estimated1RM = workoutExercise.estimated1RM,
        intensity = workoutExercise.intensity,
        improvement = workoutExercise.improvement,
    )

fun WorkoutWithAllDetails.toDomain(): DomainWorkout =
    DomainWorkout(
        id = workout.workoutId,
        date = workout.workoutDate,
        exercises = exercises.map { it.toDomain() },
        notes = workout.notes?.split("\n"),
    )

fun SystemConfig.toDomain(): DomainSystemConfig =
    DomainSystemConfig(
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        updatedAt = updatedAt,
        weightUnit = Weight.valueOf(weightUnit),
        preferredLlmProvider = LLMProvider.valueOf(preferredLlmProvider),
        themePreference = ThemePreference.valueOf(themePreference),
        isDynamicTheme = isDynamicTheme,
    )

fun ScribbleEntity.toDomain(): Scribble {
    return Scribble(
        id = scribbleId,
        rawText = rawText,
        parsedJson = parsedJson,
        status = runCatching { ScribbleStatus.valueOf(status.uppercase()) }.getOrDefault(
            ScribbleStatus.FAILED
        ),
        createdAt = createdAt,
        workoutId = workoutId,
        exercises = emptyList() // Base entity doesn't have exercises, use ScribbleWithExercises
    )
}

fun ScribbleWithExercises.toDomain(): Scribble {
    return Scribble(
        id = scribble.scribbleId,
        rawText = scribble.rawText,
        parsedJson = scribble.parsedJson,
        status = runCatching { ScribbleStatus.valueOf(scribble.status.uppercase()) }.getOrDefault(
            ScribbleStatus.FAILED
        ),
        createdAt = scribble.createdAt,
        workoutId = scribble.workoutId,
        exercises = exercises.map { it.toDomain() },
    )
}

/**
 * Mapper extension functions to convert domain objects into Room entities.
 */

fun Scribble.toEntity(): ScribbleEntity =
    ScribbleEntity(
        scribbleId = id,
        rawText = rawText,
        parsedJson = parsedJson,
        status = status.name,
        createdAt = createdAt,
        workoutId = workoutId,
    )

fun DomainSet.toEntity(workoutExerciseId: Long): WorkoutSet =
    WorkoutSet(
        setId = id,
        workoutExerciseId = workoutExerciseId,
        setNumber = setNumber,
        weight = weight,
        reps = reps,
        rpe = rpe,
        notes = notes,
    )

fun DomainExercise.toEntity(): Exercise =
    Exercise(
        exerciseId = id,
        name = canonicalName,
        muscleGroup = muscleGroup,
        isDraft = isDraft,
    )

fun DomainWorkout.toEntity(): Workout =
    Workout(
        workoutId = id,
        workoutDate = date,
        notes = notes?.joinToString("\n"),
    )

fun DomainSystemConfig.toEntity(): SystemConfig =
    SystemConfig(
        id = 0, // Single row with fixed key
        summaryPrompt = summaryPrompt,
        suggestionPrompt = suggestionPrompt,
        insightPrompt = insightPrompt,
        parsePrompt = parsePrompt,
        preferredLlmProvider = preferredLlmProvider.name,
        updatedAt = updatedAt,
        weightUnit = weightUnit.name,
        themePreference = themePreference.name,
        isDynamicTheme = isDynamicTheme,
    )
