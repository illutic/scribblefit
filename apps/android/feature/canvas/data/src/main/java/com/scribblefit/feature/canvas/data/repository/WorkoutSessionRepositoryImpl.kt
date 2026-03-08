package com.scribblefit.feature.canvas.data.repository

import com.scribblefit.feature.canvas.domain.model.WorkoutSession
import com.scribblefit.feature.canvas.domain.repository.WorkoutSessionRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutSessionRepositoryImpl @Inject constructor() : WorkoutSessionRepository {
    override suspend fun getActiveSession(): WorkoutSession? = null
    override suspend fun updateSession(session: WorkoutSession) {}
    override suspend fun clearActiveSession() {}
}
