package com.scribblefit.core.database.di

import android.content.Context
import androidx.room.Room
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.InsightsCacheDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesScribbleFitDatabase(
        @ApplicationContext context: Context
    ): ScribbleFitDatabase {
        return Room.databaseBuilder(
            context,
            ScribbleFitDatabase::class.java,
            "scribblefit-database"
        ).build()
    }

    @Provides
    fun providesSyncQueueDao(
        database: ScribbleFitDatabase
    ): SyncQueueDao = database.syncQueueDao()

    @Provides
    fun providesWorkoutLogDao(
        database: ScribbleFitDatabase
    ): WorkoutLogDao = database.workoutLogDao()

    @Provides
    fun providesSetDao(
        database: ScribbleFitDatabase
    ): SetDao = database.setDao()

    @Provides
    fun providesExerciseDictionaryDao(
        database: ScribbleFitDatabase
    ): ExerciseDictionaryDao = database.exerciseDictionaryDao()

    @Provides
    fun providesSystemConfigDao(
        database: ScribbleFitDatabase
    ): SystemConfigDao = database.systemConfigDao()

    @Provides
    fun providesInsightsCacheDao(
        database: ScribbleFitDatabase
    ): InsightsCacheDao = database.insightsCacheDao()
}
