package com.scribblefit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scribblefit.core.database.converter.StringListConverter
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.entity.ExerciseEntity
import com.scribblefit.core.database.entity.InsightsCacheEntity
import com.scribblefit.core.database.entity.SetEntity
import com.scribblefit.core.database.entity.ScribbleEntity
import com.scribblefit.core.database.entity.SystemConfigEntity
import com.scribblefit.core.database.entity.WorkoutEntity

private const val DATABASE_VERSION = 1

@Database(
    entities = [
        ScribbleEntity::class,
        WorkoutEntity::class,
        SetEntity::class,
        ExerciseEntity::class,
        SystemConfigEntity::class,
        InsightsCacheEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class ScribbleFitDatabase : RoomDatabase() {
    abstract fun scribbleDao(): ScribbleDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun setDao(): SetDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun insightsCacheDao(): InsightsCacheDao
}
