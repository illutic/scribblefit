package com.scribblefit.feature.ai.data.repository

import com.scribblefit.core.database.dao.ActiveSessionDao
import com.scribblefit.core.database.model.ActiveSessionEntity
import com.scribblefit.feature.ai.data.mapper.*
import com.scribblefit.feature.ai.domain.model.WorkoutSession
import com.scribblefit.feature.ai.domain.repository.WorkoutSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionRepositoryImpl @Inject constructor(
    private val activeSessionDao: ActiveSessionDao,
    private val json: Json
) : WorkoutSessionRepository {

    override fun getActiveSession(): Flow<WorkoutSession?> {
        return activeSessionDao.getActiveSession().map { entity ->
            entity?.let { json.decodeFromString<WorkoutSessionDto>(it.jsonData).toDomain() }
        }
    }

    override suspend fun updateSession(session: WorkoutSession) {
        val entity = ActiveSessionEntity(
            jsonData = json.encodeToString(session.toDto()),
            updatedAt = System.currentTimeMillis()
        )
        activeSessionDao.upsertSession(entity)
    }

    override suspend fun clearActiveSession() {
        activeSessionDao.clearSession()
    }
}
