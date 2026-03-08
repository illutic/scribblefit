package com.scribblefit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import com.scribblefit.core.database.model.InsightsCacheEntity
import com.scribblefit.core.database.model.SetEntity
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.core.database.model.SystemConfigEntity
import com.scribblefit.core.database.model.WorkoutLogEntity

@Database(
    entities = [
        SyncQueueEntity::class,
        WorkoutLogEntity::class,
        SetEntity::class,
        ExerciseDictionaryEntity::class,
        SystemConfigEntity::class,
        InsightsCacheEntity::class
    ],
    version = 2,
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
}
