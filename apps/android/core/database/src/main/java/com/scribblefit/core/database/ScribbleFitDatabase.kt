package com.scribblefit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scribblefit.core.database.converter.StringListConverter
import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.entity.ExerciseDictionaryEntity
import com.scribblefit.core.database.entity.InsightsCacheEntity
import com.scribblefit.core.database.entity.SetEntity
import com.scribblefit.core.database.entity.SyncQueueEntity
import com.scribblefit.core.database.entity.SystemConfigEntity
import com.scribblefit.core.database.entity.WorkoutLogEntity

private const val DATABASE_VERSION = 1

@Database(
    entities = [
        SyncQueueEntity::class,
        WorkoutLogEntity::class,
        SetEntity::class,
        ExerciseDictionaryEntity::class,
        SystemConfigEntity::class,
        InsightsCacheEntity::class
    ],
    version = DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class ScribbleFitDatabase : RoomDatabase() {
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun setDao(): SetDao
    abstract fun exerciseDictionaryDao(): ExerciseDictionaryDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun insightsCacheDao(): InsightsCacheDao
}
