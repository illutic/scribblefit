package com.scribblefit.feature.canvas.domain.repository

import com.scribblefit.feature.canvas.domain.model.WorkoutSession

interface WorkoutSessionRepository {
    suspend fun getActiveSession(): WorkoutSession?
    suspend fun updateSession(session: WorkoutSession)
    suspend fun clearActiveSession()
}
