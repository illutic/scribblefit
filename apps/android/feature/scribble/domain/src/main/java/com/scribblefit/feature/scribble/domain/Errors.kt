package com.scribblefit.feature.scribble.domain

sealed class ScribbleError : Throwable() {
    class EmptyScribbleTextException : ScribbleError()
    data class NotFound(val scribbleId: Long) : ScribbleError()
    data class ParsingFailed(val originalError: Throwable? = null) : ScribbleError()
    data class PersistenceFailed(val originalError: Throwable? = null) : ScribbleError()
}

