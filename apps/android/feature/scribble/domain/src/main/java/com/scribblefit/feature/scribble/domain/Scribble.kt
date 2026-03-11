package com.scribblefit.feature.scribble.domain

import com.scribblefit.feature.workout.domain.Exercise
import com.scribblefit.feature.ai.domain.model.ParsedWorkoutResult

sealed interface Scribble {
    val id: String
    val createdAt: Long

    data class Raw(
        override val id: String,
        override val createdAt: Long,
        val rawText: String,
        val status: SyncStatus
    ) : Scribble

    data class Parsed(
        override val id: String,
        override val createdAt: Long,
        val value: Exercise
    ) : Scribble

    data class Insight(
        override val id: String,
        override val createdAt: Long,
        val displayText: String,
        val textValue: String
    ) : Scribble
}

sealed interface SyncStatus {
    data object Pending : SyncStatus
    data object Failed : SyncStatus
    data object Logged : SyncStatus
    data class Completed(
        val parsedResult: ParsedWorkoutResult
    ) : SyncStatus

}
