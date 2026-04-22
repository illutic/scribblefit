package com.scribblefit.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class TrendDirection {
    IMPROVING, STABLE, PLATEAUED, DECLINING;

    companion object {
        fun fromString(value: String): TrendDirection {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: STABLE
        }
    }
}
