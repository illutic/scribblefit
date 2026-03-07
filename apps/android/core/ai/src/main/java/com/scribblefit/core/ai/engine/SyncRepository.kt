package com.scribblefit.core.ai.engine

import com.scribblefit.core.ai.model.ParsedWorkout
import com.scribblefit.core.ai.model.SyncItem
import com.scribblefit.core.ai.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun getPendingSyncItems(): Flow<List<SyncItem>>
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
    suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout)
    suspend fun enqueueScribble(rawText: String)
}
