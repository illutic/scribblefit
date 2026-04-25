package com.scribblefit.core.database.di

import android.content.Context
import androidx.room.Room
import com.scribblefit.core.database.ScribbleFitDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

private const val DATABASE_NAME = "scribblefit.db"

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ScribbleFitDatabase =
        Room.databaseBuilder(context, ScribbleFitDatabase::class.java, DATABASE_NAME).build()
}
