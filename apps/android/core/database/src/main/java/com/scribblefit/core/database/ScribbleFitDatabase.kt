package com.scribblefit.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scribblefit.core.database.dao.*
import com.scribblefit.core.database.model.*

@Database(
    entities = [
        SyncQueueEntity::class,
        WorkoutLogEntity::class,
        SetEntity::class,
        ExerciseDictionaryEntity::class,
        SystemConfigEntity::class,
        InsightsCacheEntity::class,
        CanvasFeedEntity::class,
        ActiveSessionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ScribbleFitDatabase : RoomDatabase() {
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun setDao(): SetDao
    abstract fun exerciseDictionaryDao(): ExerciseDictionaryDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun insightsCacheDao(): InsightsCacheDao
    abstract fun canvasFeedDao(): CanvasFeedDao
    abstract fun activeSessionDao(): ActiveSessionDao
}
