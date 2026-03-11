package com.scribblefit.feature.canvas.domain.usecase

import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import com.scribblefit.feature.scribble.domain.Scribble
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import com.scribblefit.feature.scribble.domain.SyncStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class ConfirmWorkoutUseCase(
    private val scribbleRepository: ScribbleRepository,
    private val ledgerRepository: LedgerRepository,
    private val coroutineDispatcher: CoroutineDispatcher
) {
    suspend fun execute(parsed: Scribble.Parsed) = withContext(coroutineDispatcher) {
        val exercise = parsed.value

        ledgerRepository.logWorkout(exercise)
        this@ConfirmWorkoutUseCase.scribbleRepository.updateSyncStatus(parsed.id, SyncStatus.Logged)
    }
}
