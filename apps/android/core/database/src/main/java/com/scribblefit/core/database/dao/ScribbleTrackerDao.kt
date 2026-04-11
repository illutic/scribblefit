package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
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
    @Query(
        """
        DELETE FROM workout_exercise 
        WHERE workoutExerciseId IN (SELECT workoutExerciseId FROM scribble_exercise WHERE scribbleId = :scribbleId)
        """,
    )
    suspend fun clearScribbleExercises(scribbleId: Long)

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
    @Query("SELECT * FROM scribbles WHERE ABS(createdAt - :date) < 86400000")
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
