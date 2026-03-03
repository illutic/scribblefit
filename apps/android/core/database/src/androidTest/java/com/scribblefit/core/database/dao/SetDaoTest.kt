package com.scribblefit.core.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.scribblefit.core.database.ScribbleFitDatabase
import com.scribblefit.core.database.model.ExerciseDictionaryEntity
import com.scribblefit.core.database.model.SetEntity
import com.scribblefit.core.database.model.WorkoutLogEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SetDaoTest {
    private lateinit var setDao: SetDao
    private lateinit var workoutLogDao: WorkoutLogDao
    private lateinit var exerciseDictionaryDao: ExerciseDictionaryDao
    private lateinit var db: ScribbleFitDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ScribbleFitDatabase::class.java
        ).build()
        setDao = db.setDao()
        workoutLogDao = db.workoutLogDao()
        exerciseDictionaryDao = db.exerciseDictionaryDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun upsertAndGetSetsForWorkout() = runTest {
        // Need to insert parent entities first due to Foreign Key constraints
        val workoutId = "workout1"
        val workout = WorkoutLogEntity(workoutId, System.currentTimeMillis(), "Gym", 0.0)
        workoutLogDao.upsertWorkoutLog(workout)

        val exerciseId = "ex1"
        val exercise = ExerciseDictionaryEntity(exerciseId, "Bench Press", "Chest", emptyList())
        exerciseDictionaryDao.upsertExercise(exercise)

        val set1 = SetEntity("set1", workoutId, exerciseId, 135.0, 5, null, null)
        val set2 = SetEntity("set2", workoutId, exerciseId, 135.0, 5, null, null)

        setDao.upsertSets(listOf(set1, set2))

        val sets = setDao.getSetsForWorkout(workoutId).first()
        assertEquals(2, sets.size)
        assertEquals("set1", sets[0].id)
        assertEquals("set2", sets[1].id)
    }
}
