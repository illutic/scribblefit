package com.scribblefit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.dao.WorkoutTrackerDao
import com.scribblefit.core.database.entity.config.SystemConfig
import com.scribblefit.core.database.entity.exercise.Exercise
import com.scribblefit.core.database.entity.exercise.WorkoutExercise
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.set.WorkoutSet
import com.scribblefit.core.database.entity.workout.Workout

private const val DATABASE_VERSION = 1

@Database(
    entities = [
        Workout::class,
        Exercise::class,
        WorkoutExercise::class,
        WorkoutSet::class,
        ScribbleEntity::class,
        SystemConfig::class
    ],
    version = DATABASE_VERSION,
    exportSchema = true
)
abstract class ScribbleFitDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutTrackerDao(): WorkoutTrackerDao
    abstract fun scribbleDao(): ScribbleDao
    abstract fun systemConfigDao(): SystemConfigDao
}
