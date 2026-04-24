package com.scribblefit.feature.ai.domain

import com.scribblefit.core.model.Exercise

data class ParsedWorkoutResult(
    val rawText: String,
    val exercises: List<Exercise>,
)
