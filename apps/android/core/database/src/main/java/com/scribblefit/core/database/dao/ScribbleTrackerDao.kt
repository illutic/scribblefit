package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scribblefit.core.database.entity.scribble.ScribbleExercise
import com.scribblefit.core.database.entity.scribble.ScribbleWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface ScribbleTrackerDao {
    @Insert
    suspend fun insertScribbleExercise(scribbleExercise: ScribbleExercise): Long

    @Insert
    suspend fun insertScribbleExercises(scribbleExercises: List<ScribbleExercise>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<com.scribblefit.core.database.entity.exercise.Exercise>): List<Long>

    @Query("SELECT exerciseId FROM exercise WHERE name = :name")
    suspend fun getExerciseIdByName(name: String): Long?

    @Insert
    suspend fun insertWorkoutExercises(workoutExercises: List<com.scribblefit.core.database.entity.exercise.WorkoutExercise>): List<Long>

    @Insert
    suspend fun insertWorkoutSets(workoutSets: List<com.scribblefit.core.database.entity.set.WorkoutSet>)

    @Transaction
    suspend fun insertScribbleExercisesWithDetails(
        scribbleId: Long,
        exercises: List<com.scribblefit.core.database.entity.exercise.Exercise>,
        workoutExercises: List<com.scribblefit.core.database.entity.exercise.WorkoutExercise>,
        setsPerExercise: List<List<com.scribblefit.core.database.entity.set.WorkoutSet>>,
    ) {
        exercises.forEachIndexed { index, exercise ->
            val existingId = getExerciseIdByName(exercise.name)
            val exerciseId = existingId ?: insertExercises(listOf(exercise)).first()

            val we = workoutExercises[index].copy(exerciseId = exerciseId)
            val weId = insertWorkoutExercises(listOf(we)).first()

            val sets = setsPerExercise.getOrNull(index)?.map { it.copy(workoutExerciseId = weId) }
            if (!sets.isNullOrEmpty()) {
                insertWorkoutSets(sets)
            }

            insertScribbleExercise(ScribbleExercise(scribbleId = scribbleId, workoutExerciseId = weId))
        }
    }

    @Transaction
    @Query(
        """
        DELETE FROM scribble_exercise WHERE scribbleId = :scribbleId
        AND workoutExerciseId = :workoutExerciseId
        """,
    )
    suspend fun deleteScribbleExercise(
        scribbleId: Long,
        workoutExerciseId: Long,
    )

    @Transaction
    suspend fun clearScribbleExercises(scribbleId: Long) {
        val workoutExerciseIds = getWorkoutExerciseIdsForScribble(scribbleId)
        deleteScribbleExercisesByScribbleId(scribbleId)
        workoutExerciseIds.forEach { deleteOrphanedWorkoutExerciseById(it) }
    }

    @Query("SELECT workoutExerciseId FROM scribble_exercise WHERE scribbleId = :scribbleId")
    suspend fun getWorkoutExerciseIdsForScribble(scribbleId: Long): List<Long>

    @Query("DELETE FROM scribble_exercise WHERE scribbleId = :scribbleId")
    suspend fun deleteScribbleExercisesByScribbleId(scribbleId: Long)

    @Query("DELETE FROM workout_exercise WHERE workoutExerciseId = :id AND workoutId IS NULL")
    suspend fun deleteOrphanedWorkoutExerciseById(id: Long)

    /**
     * Gets a scribble with all its associated exercises and their sets.
     */
    @Transaction
    @Query("SELECT * FROM scribbles WHERE scribbleId = :scribbleId")
    fun getScribbleWithExercises(scribbleId: Long): Flow<ScribbleWithExercises>

    /**
     * Gets all scribbles for a specific date with their associated exercises.
     * Includes fuzzy date matching and excludes completed ones if needed, 
     * but usually for Canvas we want all of them for that date.
     * Let's stick to the fuzzy range logic but allow all statuses.
     */
    @Transaction
    @Query("SELECT * FROM scribbles WHERE createdAt >= :date AND createdAt < :date + (24 * 60 * 60 * 1000)")
    fun getAllScribblesWithExercisesByDate(date: Long): Flow<List<ScribbleWithExercises>>

    @Transaction
    @Query("SELECT * FROM scribbles")
    fun getAllScribblesWithExercises(): Flow<List<ScribbleWithExercises>>

    /**
     * Gets pending scribbles for a specific date with their associated exercises.
     */
    @Transaction
    @Query("SELECT * FROM scribbles WHERE status = :status AND createdAt = :date")
    fun getScribblesWithExercisesByStatusAndDate(
        status: String,
        date: Long,
    ): Flow<List<ScribbleWithExercises>>
}
