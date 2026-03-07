package com.scribblefit.core.ai.engine

interface ConfigRepository {
    suspend fun syncMetadata(): Result<Unit>
    suspend fun syncExercises(): Result<Unit>
}
