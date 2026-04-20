package com.scribblefit.feature.scribble.domain.error

import com.scribblefit.core.model.ScribbleStatus

sealed class ScribbleError : Throwable() {
    data class InvalidStatus(val status: ScribbleStatus) : ScribbleError() {
        override val message: String = "Cannot perform this action on scribble with status $status"
    }
    object EmptyText : ScribbleError() {
        override val message: String = "Scribble text cannot be empty"
    }
    data class DatabaseError(val original: Throwable? = null) : ScribbleError() {
        override val message: String = "Database operation failed: ${original?.message ?: "Unknown error"}"
    }
}
