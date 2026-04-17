package com.scribblefit.feature.scribble.domain

sealed class ScribbleError : Throwable() {
    data object EmptyText : ScribbleError()
    data class NotFound(val scribbleId: Long) : ScribbleError()
    data class ParsingFailed(val originalError: Throwable? = null) : ScribbleError()
    data class PersistenceFailed(val originalError: Throwable? = null) : ScribbleError()
}

class EmptyScribbleTextException : IllegalArgumentException("Scribble text cannot be empty")

class ScribbleNotFoundException(scribbleId: Long) :
    IllegalArgumentException("Scribble with id $scribbleId not found")
