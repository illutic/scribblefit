package com.scribblefit.core.database.converter

import androidx.room.TypeConverter

class StringListConverter {
    @TypeConverter
    fun fromJson(value: String): List<String> =
        if (value.isEmpty()) emptyList()
        else value.split(LIST_SEPARATOR)

    @TypeConverter
    fun toJson(list: List<String>): String = list.joinToString(LIST_SEPARATOR)

    companion object {
        private const val LIST_SEPARATOR = "|||"
    }
}
