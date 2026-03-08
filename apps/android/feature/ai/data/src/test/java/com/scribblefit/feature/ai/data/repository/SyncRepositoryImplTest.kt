package com.scribblefit.feature.ai.data.repository

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.feature.ai.domain.model.ParsedExercise
import com.scribblefit.feature.ai.domain.model.ParsedSet
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncStatus
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import com.scribblefit.core.database.model.SyncStatus as EntitySyncStatus

class SyncRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var syncQueueDao: SyncQueueDao
    private lateinit var workManager: WorkManager
    private lateinit var json: Json
    private lateinit var repository: SyncRepositoryImpl

    @Before
    fun setup() {
        workManager = mockk(relaxed = true)
        context = mockk(relaxed = true)
        json = Json { ignoreUnknownKeys = true }

        syncQueueDao = mockk(relaxed = true)

        repository = SyncRepositoryImpl(context, syncQueueDao, json)
        repository.workManagerProvider = { workManager }
    }

    @Test
    fun `getPendingSyncItems maps entities to domain`() = runTest {
        // Given
        val entities = listOf(
            SyncQueueEntity("1", "SCRIBBLE", "raw", EntitySyncStatus.PENDING, 123L)
        )
        every { syncQueueDao.getSyncItemsByStatus(EntitySyncStatus.PENDING) } returns flowOf(
            entities
        )

        // When
        val result = repository.getPendingSyncItems().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
        assertEquals("SCRIBBLE", result[0].type)
        assertEquals(SyncStatus.PENDING, result[0].status)
    }

    @Test
    fun `saveParsedWorkout updates sync queue with JSON result`() = runTest {
        // Given
        val syncItemId = "sync1"
        val workout = ParsedWorkout(
            date = "2024-05-20",
            location = "Gym",
            exercises = listOf(
                ParsedExercise(
                    canonicalName = "Bench Press",
                    sets = listOf(
                        ParsedSet(100.0, 5)
                    )
                )
            )
        )

        // When
        repository.saveParsedWorkout(syncItemId, workout)

        // Then
        val expectedJson = json.encodeToString(workout)
        coVerify {
            syncQueueDao.updateParsedResult(syncItemId, EntitySyncStatus.COMPLETED, expectedJson)
        }
    }

    @Test
    fun `enqueueScribble saves item with provided id and triggers workmanager`() = runTest {
        // When
        repository.enqueueScribble("custom-id", "bench 100x5")

        // Then
        coVerify { syncQueueDao.upsertSyncItem(match { it.id == "custom-id" && it.rawText == "bench 100x5" && it.type == "SCRIBBLE" }) }
        coVerify { workManager.enqueue(any<WorkRequest>()) }
    }

    @Test
    fun `saveFeedItem saves non-scribble item`() = runTest {
        // When
        repository.saveFeedItem("prompt-1", "PROMPT", "{\"text\":\"Hello\"}", SyncStatus.COMPLETED)

        // Then
        coVerify { syncQueueDao.upsertSyncItem(match { it.id == "prompt-1" && it.type == "PROMPT" && it.parsedJson == "{\"text\":\"Hello\"}" }) }
    }
}
