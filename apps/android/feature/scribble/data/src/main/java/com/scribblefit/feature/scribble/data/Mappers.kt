package com.scribblefit.feature.scribble.data

import com.scribblefit.core.database.entity.EntitySyncStatus
import com.scribblefit.core.database.entity.ExerciseEntity
import com.scribblefit.core.database.entity.ScribbleEntity
import com.scribblefit.core.database.entity.SetEntity
import com.scribblefit.core.database.entity.WorkoutEntity
import com.scribblefit.feature.workout.domain.Exercise
import com.scribblefit.feature.workout.domain.Set
import com.scribblefit.feature.workout.domain.Workout
import com.scribblefit.feature.scribble.domain.Scribble
import com.scribblefit.feature.scribble.domain.SyncStatus
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


internal fun EntitySyncStatus.toDomain(): SyncStatus = when (this) {
    EntitySyncStatus.PENDING -> SyncStatus.Pending
    EntitySyncStatus.FAILED -> SyncStatus.Failed
    EntitySyncStatus.COMPLETED -> SyncStatus.Logged
}

internal fun SyncStatus.toEntity(): EntitySyncStatus = when (this) {
    SyncStatus.Pending -> EntitySyncStatus.PENDING
    SyncStatus.Failed -> EntitySyncStatus.FAILED
    SyncStatus.Logged -> EntitySyncStatus.COMPLETED
    // A completed status in the domain means that there is exercise data available,
    // so we can mark it as completed in the entity.
    // We will fetch the exercise data separately and update the scribble
    // with the exercise ID to ensure data integrity.
    is SyncStatus.Completed -> EntitySyncStatus.COMPLETED
}

internal fun ExerciseEntity.toDomain(sets: List<Set>) = Exercise(
    canonicalName = canonicalName,
    muscleGroup = muscleGroup,
    sets = sets
)

internal fun SetEntity.toDomain() = Set(
    weight = weight,
    reps = reps,
    rpe = rpe,
    notes = notes
)

internal fun ScribbleEntity.toDomain() = Scribble.Raw(
    id = id,
    createdAt = createdAt,
    rawText = rawText,
    status = status.toDomain()
)

@OptIn(ExperimentalUuidApi::class)
internal fun Exercise.toEntity() = ExerciseEntity(
    id = Uuid.random().toString(),
    canonicalName = canonicalName,
    muscleGroup = muscleGroup
)

@OptIn(ExperimentalUuidApi::class)
internal fun Set.toEntity(
    exerciseId: String,
    workoutId: String
) = SetEntity(
    id = Uuid.random().toString(),
    exerciseId = exerciseId,
    workoutId = workoutId,
    weight = weight,
    reps = reps,
    rpe = rpe,
    notes = notes
)

@OptIn(ExperimentalUuidApi::class)
internal fun Workout.toEntity() = WorkoutEntity(
    id = Uuid.random().toString(),
    date = date,
    totalVolume = exercises.sumOf { exercise ->
        exercise.sets.sumOf { set ->
            set.weight * set.reps
        }
    }
)
