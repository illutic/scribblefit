package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scribblefit.core.database.entity.exercise.Exercise
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.set.WorkoutSet
import com.scribblefit.core.database.entity.workout.Workout
import com.scribblefit.core.database.entity.workout.WorkoutWithAllDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercises(exercises: List<Exercise>): List<Long>

    @Insert
    suspend fun insertWorkoutExercises(workoutExercises: List<WorkoutExercise>): List<Long>

    @Insert
    suspend fun insertWorkoutSets(workoutSets: List<WorkoutSet>)

    @Query("SELECT exerciseId FROM exercise WHERE name = :name")
    suspend fun getExerciseIdByName(name: String): Long?

    /**
     * Atomically inserts a workout with all its exercises and sets.
     * Resolves exercise IDs by name (IGNORE on conflict) before linking.
     */
    @Transaction
    suspend fun insertWorkoutWithDetails(
        workout: Workout,
        exercises: List<Exercise>,
        setsPerExercise: List<List<WorkoutSet>>,
    ): Long {
        val workoutId = insertWorkout(workout)

        val insertResults = insertExercises(exercises)
        val exerciseIds = insertResults.mapIndexed { index, id ->
            if (id == -1L) getExerciseIdByName(exercises[index].name) ?: -1L else id
        }

        val workoutExercises = exerciseIds.map { exerciseId ->
            WorkoutExercise(workoutId = workoutId, exerciseId = exerciseId)
        }
        val workoutExerciseIds = insertWorkoutExercises(workoutExercises)

        val allSets = workoutExerciseIds.flatMapIndexed { index, weId ->
            setsPerExercise.getOrElse(index) { emptyList() }.map { set ->
                set.copy(workoutExerciseId = weId)
            }
        }
        if (allSets.isNotEmpty()) {
            insertWorkoutSets(allSets)
        }

        return workoutId
    }

    @Transaction
    @Query("SELECT * FROM workout WHERE workoutId = :workoutId")
    fun getWorkoutWithAllDetails(workoutId: Long): Flow<WorkoutWithAllDetails>
@Transaction
@Query("SELECT * FROM workout WHERE workoutDate = :date LIMIT 1")
fun getWorkoutByDate(date: Long): Flow<Workout?>

@Transaction
@Query("SELECT * FROM workout WHERE workoutDate BETWEEN :startDate AND :endDate ORDER BY workoutDate ASC")
fun getWorkoutsWithAllDetailsInRange(startDate: Long, endDate: Long): Flow<List<WorkoutWithAllDetails>>

@Transaction
@Query("SELECT * FROM workout ORDER BY workoutDate ASC")
fun getAllWorkoutsWithAllDetails(): Flow<List<WorkoutWithAllDetails>>

@Transaction
suspend fun clearAllUserData() {
    deleteWorkoutSets()
    deleteScribbleExercises()
    deleteWorkoutExercises()
    deleteWorkouts()
    deleteExercises()
    deleteScribbles()
}

@Query("DELETE FROM workout_set")
suspend fun deleteWorkoutSets()

@Query("DELETE FROM scribble_exercise")
suspend fun deleteScribbleExercises()

@Query("DELETE FROM workout_exercise")
suspend fun deleteWorkoutExercises()

@Query("DELETE FROM workout")
suspend fun deleteWorkouts()

@Query("DELETE FROM exercise")
suspend fun deleteExercises()

@Query("DELETE FROM scribbles")
suspend fun deleteScribbles()
}
