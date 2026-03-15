package com.scribblefit.feature.scribble.domain


class EmptyScribbleTextException : IllegalArgumentException("Scribble text cannot be empty")

class ScribbleNotFoundException(scribbleId: Long) :
    IllegalArgumentException("Scribble with id $scribbleId not found")
