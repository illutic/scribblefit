package com.scribblefit.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.entity.config.SystemConfigEntity
import com.scribblefit.core.database.entity.exercise.ExerciseEntity
import com.scribblefit.core.database.entity.scribble.ScribbleEntity
import com.scribblefit.core.database.entity.set.SetEntity
import com.scribblefit.core.database.model.InsightsCacheEntity

@Database(
    entities = [
        ScribbleEntity::class,
        ExerciseEntity::class,
        SetEntity::class,
        SystemConfigEntity::class,
        InsightsCacheEntity::class,
    ],
    version = 2,
    exportSchema = true
)
abstract class ScribbleFitDatabase : RoomDatabase() {
    abstract fun scribbleDao(): ScribbleDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun setDao(): SetDao
    abstract fun systemConfigDao(): SystemConfigDao
    abstract fun insightsCacheDao(): InsightsCacheDao

    suspend fun clearAllData() {
        scribbleDao().clearAllScribbles()
        exerciseDao().clearAllExercises()
        setDao().clearAllSets()
        systemConfigDao().clearSystemConfig()
        insightsCacheDao().clearAll()
    }
}
