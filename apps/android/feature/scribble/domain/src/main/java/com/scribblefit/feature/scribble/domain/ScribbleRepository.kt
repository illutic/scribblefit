package com.scribblefit.feature.scribble.domain

import kotlinx.coroutines.flow.Flow

interface ScribbleRepository {
    fun getAllScribbles(): Flow<List<Scribble>>
    fun getPendingScribbles(): Flow<List<Scribble.Raw>>
    suspend fun updateSyncStatus(id: String, status: SyncStatus): Result<Unit>
    suspend fun enqueueScribble(rawText: String)
}
