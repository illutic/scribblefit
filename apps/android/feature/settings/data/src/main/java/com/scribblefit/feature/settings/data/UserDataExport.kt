package com.scribblefit.feature.settings.data

import kotlinx.serialization.Serializable

@Serializable
data class UserDataExport(
    val scribbles: List<ExportScribble>
)

@Serializable
data class ExportScribble(
    val createdAt: Long,
    val exercises: List<ExportExercise>
)

@Serializable
data class ExportExercise(
    val name: String,
    val sets: List<ExportSet>
)

@Serializable
data class ExportSet(
    val setNumber: Int,
    val reps: Int,
    val weight: Float? = null,
    val rpe: Float? = null,
    val notes: String? = null
)