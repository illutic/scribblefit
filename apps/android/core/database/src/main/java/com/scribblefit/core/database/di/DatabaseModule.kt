package com.scribblefit.core.database.di

import android.content.Context
import androidx.room.Room
import com.scribblefit.core.database.ScribbleFitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val DATABASE_NAME = "scribblefit.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScribbleFitDatabase =
        Room.databaseBuilder(context, ScribbleFitDatabase::class.java, DATABASE_NAME).build()

    @Provides fun provideSyncQueueDao(db: ScribbleFitDatabase) = db.scribbleDao()
    @Provides fun provideWorkoutLogDao(db: ScribbleFitDatabase) = db.workoutDao()
    @Provides fun provideSetDao(db: ScribbleFitDatabase) = db.setDao()
    @Provides fun provideExerciseDictionaryDao(db: ScribbleFitDatabase) = db.exerciseDao()
    @Provides fun provideSystemConfigDao(db: ScribbleFitDatabase) = db.systemConfigDao()
    @Provides fun provideInsightsCacheDao(db: ScribbleFitDatabase) = db.insightsCacheDao()
}
