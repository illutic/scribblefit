package com.scribblefit.core.database

import androidx.room.TypeConverter
import com.scribblefit.core.database.model.SyncStatus
import com.scribblefit.core.ai.model.LLMProvider

class Converters {
    @TypeConverter
    fun fromLLMProvider(value: LLMProvider): String {
        return value.name
    }

    @TypeConverter
    fun toLLMProvider(value: String): LLMProvider {
        return LLMProvider.valueOf(value)
    }

    @TypeConverter
    fun fromSyncStatus(value: SyncStatus): String {
        return value.name
    }

    @TypeConverter
    fun toSyncStatus(value: String): SyncStatus {
        return SyncStatus.valueOf(value)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isEmpty()) emptyList() else value.split(",")
    }
}
