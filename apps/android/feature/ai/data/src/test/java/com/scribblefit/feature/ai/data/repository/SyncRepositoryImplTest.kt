package com.scribblefit.feature.ai.data.repository

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.scribblefit.core.database.dao.ExerciseDictionaryDao
import com.scribblefit.core.database.dao.SetDao
import com.scribblefit.core.database.dao.SyncQueueDao
import com.scribblefit.core.database.dao.WorkoutLogDao
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import com.scribblefit.core.database.model.SyncQueueEntity
import com.scribblefit.core.database.model.SyncStatus as EntitySyncStatus
import com.scribblefit.feature.ai.domain.model.ParsedExercise
import com.scribblefit.feature.ai.domain.model.ParsedSet
import com.scribblefit.feature.ai.domain.model.ParsedWorkout
import com.scribblefit.feature.ai.domain.model.SyncStatus
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

class SyncRepositoryImplTest {

    private lateinit var context: Context
    private lateinit var syncQueueDao: SyncQueueDao
    private lateinit var workoutLogDao: WorkoutLogDao
    private lateinit var setDao: SetDao
    private lateinit var exerciseDictionaryDao: ExerciseDictionaryDao
    private lateinit var workManager: WorkManager
    private lateinit var json: Json
    private lateinit var repository: SyncRepositoryImpl

    @Before
    fun setup() {
        workManager = mockk(relaxed = true)
        context = mockk(relaxed = true)
        json = Json { ignoreUnknownKeys = true }
        
        syncQueueDao = mockk(relaxed = true)
        workoutLogDao = mockk(relaxed = true)
        setDao = mockk(relaxed = true)
        exerciseDictionaryDao = mockk(relaxed = true)
        
        repository = SyncRepositoryImpl(context, syncQueueDao, workoutLogDao, setDao, exerciseDictionaryDao, json) { workManager }
    }

    @Test
    fun `getPendingSyncItems maps entities to domain`() = runTest {
        // Given
        val entities = listOf(
            SyncQueueEntity("1", "raw", EntitySyncStatus.PENDING, 123L)
        )
        every { syncQueueDao.getSyncItemsByStatus(EntitySyncStatus.PENDING) } returns flowOf(entities)

        // When
        val result = repository.getPendingSyncItems().first()

        // Then
        assertEquals(1, result.size)
        assertEquals("1", result[0].id)
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
        repository.enqueueScribble("bench 100x5", "custom-id")

        // Then
        coVerify { syncQueueDao.upsertSyncItem(match { it.id == "custom-id" && it.rawText == "bench 100x5" }) }
        coVerify { workManager.enqueue(any<WorkRequest>()) }
    }
}
