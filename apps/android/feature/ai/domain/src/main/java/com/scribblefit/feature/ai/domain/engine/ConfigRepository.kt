package com.scribblefit.feature.ai.domain.engine

interface ConfigRepository {
    suspend fun syncMetadata(): Result<Unit>
    suspend fun syncExercises(): Result<Unit>
}
