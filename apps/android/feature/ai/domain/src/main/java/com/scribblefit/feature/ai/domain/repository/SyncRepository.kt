package com.scribblefit.feature.ai.domain.repository

import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

interface SyncRepository {
    fun getPendingSyncItems(): Flow<List<SyncItem>>
    suspend fun updateSyncStatus(id: String, status: SyncStatus)
    suspend fun saveParsedWorkout(syncItemId: String, workout: ParsedWorkout)
}
