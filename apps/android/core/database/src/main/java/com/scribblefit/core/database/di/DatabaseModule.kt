package com.scribblefit.core.database.di

import android.content.Context
import androidx.room.Room
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.dao.ExerciseDao
import com.scribblefit.core.database.dao.ScribbleDao
import com.scribblefit.core.database.dao.ScribbleTrackerDao
import com.scribblefit.core.database.dao.SystemConfigDao
import com.scribblefit.core.database.dao.WorkoutDao
import com.scribblefit.core.database.dao.WorkoutExerciseDao
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

    @Provides
    fun provideWorkoutDao(database: ScribbleFitDatabase): WorkoutDao =
        database.workoutDao()

    @Provides
    fun provideExerciseDao(database: ScribbleFitDatabase): ExerciseDao =
        database.exerciseDao()

    @Provides
    fun provideWorkoutTrackerDao(database: ScribbleFitDatabase): WorkoutExerciseDao =
        database.workoutTrackerDao()

    @Provides
    fun provideScribbleDao(database: ScribbleFitDatabase): ScribbleDao =
        database.scribbleDao()

    @Provides
    fun provideScribbleTrackerDao(database: ScribbleFitDatabase): ScribbleTrackerDao =
        database.scribbleTrackerDao()

    @Provides
    fun provideSystemConfigDao(database: ScribbleFitDatabase): SystemConfigDao =
        database.systemConfigDao()
}
