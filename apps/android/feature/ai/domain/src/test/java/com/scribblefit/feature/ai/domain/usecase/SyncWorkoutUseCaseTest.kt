package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.ai.domain.repository.SyncRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncWorkoutUseCaseTest {

    private lateinit var syncRepository: SyncRepository
    private lateinit var engine: LLMEngine
    private lateinit var useCase: SyncWorkoutUseCase

    @Before
    fun setup() {
        syncRepository = mockk(relaxed = true)
        engine = mockk()
        useCase = SyncWorkoutUseCase(syncRepository, engine)
    }

    @Test
    fun `when sync is invoked, it processes all pending items`() = runTest {
        // Given
        val items = listOf(
            SyncItem("1", "Bench 135x5", SyncStatus.PENDING, 0L),
            SyncItem("2", "Squat 225x5", SyncStatus.PENDING, 0L)
        )
        coEvery { syncRepository.getPendingSyncItems() } returns flowOf(items)
        coEvery { engine.parseWorkout(any()) } returns Result.success(mockk())

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("1", SyncStatus.PROCESSING) }
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("2", SyncStatus.PROCESSING) }
        coVerify(exactly = 1) { engine.parseWorkout("Bench 135x5") }
        coVerify(exactly = 1) { engine.parseWorkout("Squat 225x5") }
        coVerify(exactly = 1) { syncRepository.saveParsedWorkout("1", any()) }
        coVerify(exactly = 1) { syncRepository.saveParsedWorkout("2", any()) }
    }

    @Test
    fun `when engine fails, it updates status to FAILED`() = runTest {
        // Given
        val items = listOf(SyncItem("1", "Bench 135x5", SyncStatus.PENDING, 0L))
        coEvery { syncRepository.getPendingSyncItems() } returns flowOf(items)
        coEvery { engine.parseWorkout(any()) } returns Result.failure(Exception("AI Error"))

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("1", SyncStatus.PROCESSING) }
        coVerify(exactly = 1) { engine.parseWorkout("Bench 135x5") }
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("1", SyncStatus.FAILED) }
        coVerify(exactly = 0) { syncRepository.saveParsedWorkout(any(), any()) }
    }
}
