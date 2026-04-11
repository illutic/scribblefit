package com.scribblefit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.scribblefit.core.database.entity.workout.Workout
import com.scribblefit.core.database.entity.workout.WorkoutWithAllDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

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
