package com.scribblefit.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.model.WorkoutLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class WorkoutLogDaoTest {
    private lateinit var workoutLogDao: WorkoutLogDao
    private lateinit var db: ScribbleFitDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ScribbleFitDatabase::class.java
        ).build()
        workoutLogDao = db.workoutLogDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeWorkoutLogAndReadById() = runTest {
        val workout = WorkoutLogEntity(
            id = "1",
            date = System.currentTimeMillis(),
            location = "Gym",
            totalVolume = 1000.0
        )
        workoutLogDao.upsertWorkoutLog(workout)
        val readWorkout = workoutLogDao.getWorkoutLogById("1").first()
        assertEquals(workout, readWorkout)
    }

    @Test
    @Throws(Exception::class)
    fun deleteWorkoutLogAndCheckNull() = runTest {
        val workout = WorkoutLogEntity(
            id = "1",
            date = System.currentTimeMillis(),
            location = "Gym",
            totalVolume = 1000.0
        )
        workoutLogDao.upsertWorkoutLog(workout)
        workoutLogDao.deleteWorkoutLog(workout)
        val readWorkout = workoutLogDao.getWorkoutLogById("1").first()
        assertNull(readWorkout)
    }

    @Test
    @Throws(Exception::class)
    fun getAllWorkoutLogsSortedByDate() = runTest {
        val workout1 = WorkoutLogEntity("1", 1000L, "Gym A", 10.0)
        val workout2 = WorkoutLogEntity("2", 2000L, "Gym B", 20.0)

        workoutLogDao.upsertWorkoutLog(workout1)
        workoutLogDao.upsertWorkoutLog(workout2)

        val allLogs = workoutLogDao.getAllWorkoutLogs().first()
        assertEquals(2, allLogs.size)
        assertEquals("2", allLogs[0].id) // Most recent first
        assertEquals("1", allLogs[1].id)
    }
}
