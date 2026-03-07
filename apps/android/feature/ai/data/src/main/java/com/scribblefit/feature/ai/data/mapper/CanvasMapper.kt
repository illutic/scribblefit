package com.scribblefit.feature.ai.data.mapper

import com.scribblefit.feature.ai.domain.model.*
import kotlinx.serialization.Serializable

@Serializable
sealed class FeedItemDto {
    @Serializable
    data class Prompt(val id: String, val timestamp: Long, val text: String, val emoji: String, val type: String) : FeedItemDto()
    @Serializable
    data class Scribble(val id: String, val timestamp: Long, val rawText: String, val status: String) : FeedItemDto()
    @Serializable
    data class Confirmation(val id: String, val timestamp: Long, val scribbleId: String) : FeedItemDto() // Workout handled separately
    @Serializable
    data class Insight(val id: String, val timestamp: Long, val text: String, val emoji: String) : FeedItemDto()
}

@Serializable
data class WorkoutSessionDto(
    val id: String,
    val startTime: Long,
    val lastActivityTime: Long,
    val exercises: List<SessionExerciseDto>
)

@Serializable
data class SessionExerciseDto(
    val canonicalName: String,
    val sets: List<SessionSetDto>
)

@Serializable
data class SessionSetDto(
    val weight: Double,
    val reps: Int,
    val rpe: Double? = null,
    val notes: String? = null
)

// Mapping extensions would go here in implementation
fun WorkoutSession.toDto() = WorkoutSessionDto(
    id = id,
    startTime = startTime,
    lastActivityTime = lastActivityTime,
    exercises = exercises.map { it.toDto() }
)

fun SessionExercise.toDto() = SessionExerciseDto(
    canonicalName = canonicalName,
    sets = sets.map { it.toDto() }
)

fun SessionSet.toDto() = SessionSetDto(weight, reps, rpe, notes)

fun WorkoutSessionDto.toDomain() = WorkoutSession(
    id = id,
    startTime = startTime,
    lastActivityTime = lastActivityTime,
    exercises = exercises.map { it.toDomain() }
)

fun SessionExerciseDto.toDomain() = SessionExercise(
    canonicalName = canonicalName,
    sets = sets.map { it.toDomain() }
)

fun SessionSetDto.toDomain() = SessionSet(weight, reps, rpe, notes)
