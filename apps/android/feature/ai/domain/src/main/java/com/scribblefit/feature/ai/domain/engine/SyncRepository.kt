package com.scribblefit.feature.ai.domain.engine

import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun getPendingSyncItems(): Flow<List<SyncItem>>
    fun getAllSyncItems(): Flow<List<SyncItem>>
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
    suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout)
    suspend fun enqueueScribble(id: String, rawText: String)
    suspend fun deleteSyncItem(id: String)
    suspend fun saveFeedItem(
        id: String,
        type: String,
        jsonData: String,
        status: SyncStatus = SyncStatus.COMPLETED
    )
    suspend fun syncWorkouts()
}
