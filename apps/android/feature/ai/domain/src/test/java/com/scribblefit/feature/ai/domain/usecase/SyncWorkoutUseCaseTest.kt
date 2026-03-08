package com.scribblefit.feature.ai.domain.usecase

import com.scribblefit.feature.ai.domain.engine.LLMEngine
import com.scribblefit.feature.ai.domain.model.AIParsingException
import com.scribblefit.feature.ai.domain.model.SyncItem
import com.scribblefit.feature.ai.domain.model.SyncStatus
import com.scribblefit.feature.ai.domain.engine.SyncRepository
import com.scribblefit.feature.ai.domain.engine.TelemetryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SyncWorkoutUseCaseTest {

    private lateinit var syncRepository: SyncRepository
    private lateinit var telemetryRepository: TelemetryRepository
    private lateinit var engine: LLMEngine
    private lateinit var configRepository: com.scribblefit.feature.ai.domain.engine.ConfigRepository
    private lateinit var useCase: SyncWorkoutUseCase
    private val promptVersion = "1.0.0"

    @Before
    fun setup() {
        syncRepository = mockk(relaxed = true)
        telemetryRepository = mockk(relaxed = true)
        configRepository = mockk(relaxed = true)
        engine = mockk()
        
        coEvery { configRepository.getConfig() } returns flowOf(mockk {
            every { promptVersion } returns this@SyncWorkoutUseCaseTest.promptVersion
        })
        
        useCase = SyncWorkoutUseCase(syncRepository, telemetryRepository, engine, configRepository)
    }

    @Test
    fun `when sync is invoked, it processes all pending items`() = runTest {
        // Given
        val items = listOf(
            SyncItem("1", "SCRIBBLE", "Bench 135x5", SyncStatus.PENDING, 0L),
            SyncItem("2", "SCRIBBLE", "Squat 225x5", SyncStatus.PENDING, 0L)
        )
        coEvery { syncRepository.getPendingSyncItems() } returns flowOf(items)
        coEvery { engine.parseWorkout(any()) } returns ParsingResult(ParsingStatus.SUCCESS, mockk())

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
    fun `when engine fails, it reports error to telemetry`() = runTest {
        // Given
        val rawText = "Bench 135x5"
        val items = listOf(SyncItem("1", "SCRIBBLE", rawText, SyncStatus.PENDING, 0L))
        val errorMsg = "JSON is malformed"
        coEvery { syncRepository.getPendingSyncItems() } returns flowOf(items)
        coEvery { engine.parseWorkout(any()) } returns ParsingResult(ParsingStatus.FAILED, error = errorMsg)

        // When
        useCase()

        // Then
        coVerify { 
            telemetryRepository.reportError(match { 
                it.rawText == rawText && it.errorMessage == errorMsg 
            }) 
        }
    }

    @Test
    fun `when engine fails with general error, it updates status to FAILED`() = runTest {
        // Given
        val items = listOf(SyncItem("1", "SCRIBBLE", "Bench 135x5", SyncStatus.PENDING, 0L))
        coEvery { syncRepository.getPendingSyncItems() } returns flowOf(items)
        coEvery { engine.parseWorkout(any()) } returns ParsingResult(ParsingStatus.FAILED, error = "AI Error")

        // When
        useCase()

        // Then
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("1", SyncStatus.PROCESSING) }
        coVerify(exactly = 1) { engine.parseWorkout("Bench 135x5") }
        coVerify(exactly = 1) { syncRepository.updateSyncStatus("1", SyncStatus.FAILED) }
        coVerify(exactly = 1) { 
            telemetryRepository.reportError(match { 
                it.rawText == "Bench 135x5" && it.promptVersion == promptVersion && it.errorMessage == "AI Error"
            }) 
        }
        coVerify(exactly = 0) { syncRepository.saveParsedWorkout(any(), any()) }
    }
}
