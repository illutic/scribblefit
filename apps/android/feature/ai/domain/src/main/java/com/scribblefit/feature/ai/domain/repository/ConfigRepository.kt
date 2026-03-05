package com.scribblefit.feature.ai.domain.repository

interface ConfigRepository {
    suspend fun syncMetadata(): Result<Unit>
    suspend fun syncExercises(): Result<Unit>
}
