package com.scribblefit.feature.ai.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.scribblefit.feature.ai.domain.usecase.SyncWorkoutUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncWorkoutUseCase: SyncWorkoutUseCase
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = runCatching {
        syncWorkoutUseCase()
    }.fold(
        onSuccess = { Result.success() },
        onFailure = {
            if (runAttemptCount < MAX_RETRIES) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    )

    companion object {
        private const val MAX_RETRIES = 3
    }
}
