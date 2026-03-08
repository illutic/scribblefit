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
    @Assisted params: WorkerParameters,
    private val syncWorkoutUseCase: SyncWorkoutUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = try {
        syncWorkoutUseCase()
        Result.success()
    } catch (e: Exception) {
        Result.retry()
    }
}
