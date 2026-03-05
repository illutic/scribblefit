package com.scribblefit.feature.ai.data.repository

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
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

class SyncRepositoryImplTest {

    private lateinit var syncQueueDao: SyncQueueDao
    private lateinit var workoutLogDao: WorkoutLogDao
    private lateinit var setDao: SetDao
    private lateinit var exerciseDictionaryDao: ExerciseDictionaryDao
    private lateinit var repository: SyncRepositoryImpl

    @Before
    fun setup() {
        syncQueueDao = mockk(relaxed = true)
        workoutLogDao = mockk(relaxed = true)
        setDao = mockk(relaxed = true)
        exerciseDictionaryDao = mockk(relaxed = true)
        repository = SyncRepositoryImpl(syncQueueDao, workoutLogDao, setDao, exerciseDictionaryDao)
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
    fun `saveParsedWorkout saves workout and sets with mapped IDs`() = runTest {
        // Given
        val syncItemId = "sync1"
        val now = Instant.now()
        val workout = ParsedWorkout(
            date = now.toString(),
            location = "Gym",
            exercises = listOf(
                ParsedExercise(
                    canonicalName = "Bench Press",
                    sets = listOf(ParsedSet(135.0, 5))
                )
            )
        )
        val exerciseEntity = ExerciseDictionaryEntity("ex1", "Bench Press", "Chest", emptyList())
        
        every { exerciseDictionaryDao.searchExercises("Bench Press") } returns flowOf(listOf(exerciseEntity))

        // When
        repository.saveParsedWorkout(syncItemId, workout)

        // Then
        coVerify { workoutLogDao.upsertWorkoutLog(match { it.location == "Gym" && it.date == now.toEpochMilli() }) }
        coVerify { setDao.upsertSets(match { it.size == 1 && it[0].exerciseId == "ex1" }) }
        coVerify { syncQueueDao.updateStatus(syncItemId, EntitySyncStatus.COMPLETED) }
    }

    @Test
    fun `saveParsedWorkout handles invalid date string`() = runTest {
        // Given
        val workout = ParsedWorkout(
            date = "invalid-date",
            location = null,
            exercises = emptyList()
        )
        every { exerciseDictionaryDao.searchExercises(any()) } returns flowOf(emptyList())

        // When
        repository.saveParsedWorkout("sync1", workout)

        // Then
        coVerify { workoutLogDao.upsertWorkoutLog(any()) } // Should still save with fallback date
    }
}
