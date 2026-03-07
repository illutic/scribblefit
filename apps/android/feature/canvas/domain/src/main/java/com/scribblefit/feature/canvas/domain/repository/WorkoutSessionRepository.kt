package com.scribblefit.feature.canvas.domain.repository

import com.scribblefit.feature.canvas.domain.model.WorkoutSession
import kotlinx.coroutines.flow.Flow

interface WorkoutSessionRepository {
    /**
     * Observes the current active, uncommitted workout session.
     */
    fun getActiveSession(): Flow<WorkoutSession?>

    /**
     * Updates or creates an active session with new data from the AI parser.
     */
    suspend fun updateSession(session: WorkoutSession)

    /**
     * Clears the active session after it has been committed to the ledger.
     */
    suspend fun clearActiveSession()
}
