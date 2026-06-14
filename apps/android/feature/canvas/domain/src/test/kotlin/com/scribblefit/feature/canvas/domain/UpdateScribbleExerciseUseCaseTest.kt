package com.scribblefit.feature.canvas.domain

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.core.model.Set
import com.scribblefit.feature.exercises.domain.usecase.UpdateExerciseUseCase
import com.scribblefit.feature.sets.domain.usecase.RemoveSetUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UpdateScribbleExerciseUseCaseTest {

    private val updateExerciseUseCase: UpdateExerciseUseCase = mockk(relaxed = true)
    private val removeSetUseCase: RemoveSetUseCase = mockk(relaxed = true)
    private lateinit var useCase: UpdateScribbleExerciseUseCase

    private fun set(id: Long, setNumber: Int, reps: Int = 10, weight: Float = 80f) =
        Set(id = id, setNumber = setNumber, reps = reps, weight = weight)

    private fun exercise(id: Long, sets: List<Set> = emptyList()) = Exercise(
        id = id, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = sets, createdAt = 1_000_000L
    )

    private fun scribble(exercises: List<Exercise>) = Scribble(
        id = 1L, rawText = "raw text", status = ScribbleStatus.SUCCESS,
        createdAt = 1_000_000L, exercises = exercises
    )

    @Before
    fun setup() {
        useCase = UpdateScribbleExerciseUseCase(
            updateExerciseUseCase = updateExerciseUseCase,
            removeSetUseCase = removeSetUseCase
        )
    }

    // updateExerciseName tests
    @Test
    fun `updateExerciseName calls updateExerciseUseCase with new name`() = runTest {
        val exerciseToUpdate = exercise(5L)
        val scribble = scribble(listOf(exerciseToUpdate))
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase.updateExerciseName(scribble, 5L, "Incline Bench Press")

        assertEquals("Incline Bench Press", capturedExercises[0].canonicalName)
    }

    @Test
    fun `updateExerciseName does nothing when exercise not found in scribble`() = runTest {
        val scribble = scribble(listOf(exercise(5L)))

        useCase.updateExerciseName(scribble, 99L, "New Name")

        coVerify(exactly = 0) { updateExerciseUseCase(any()) }
    }

    // updateSetWeight tests
    @Test
    fun `updateSetWeight updates the correct set in the exercise`() = runTest {
        val sets = listOf(set(1L, 1, weight = 80f), set(2L, 2, weight = 90f))
        val ex = exercise(5L, sets)
        val scribble = scribble(listOf(ex))
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase.updateSetWeight(scribble, 5L, 1L, "100")

        val updatedSets = capturedExercises[0].sets
        assertEquals(100f, updatedSets.first { it.id == 1L }.weight)
        assertEquals(90f, updatedSets.first { it.id == 2L }.weight) // unchanged
    }

    @Test
    fun `updateSetWeight does nothing when weight is not a valid float`() = runTest {
        val ex = exercise(5L, listOf(set(1L, 1)))
        val scribble = scribble(listOf(ex))

        useCase.updateSetWeight(scribble, 5L, 1L, "invalid")

        coVerify(exactly = 0) { updateExerciseUseCase(any()) }
    }

    @Test
    fun `updateSetWeight does nothing when exercise not found`() = runTest {
        val scribble = scribble(listOf(exercise(5L)))

        useCase.updateSetWeight(scribble, 99L, 1L, "100")

        coVerify(exactly = 0) { updateExerciseUseCase(any()) }
    }

    // updateSetReps tests
    @Test
    fun `updateSetReps updates the correct set reps`() = runTest {
        val sets = listOf(set(1L, 1, reps = 10), set(2L, 2, reps = 8))
        val ex = exercise(5L, sets)
        val scribble = scribble(listOf(ex))
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase.updateSetReps(scribble, 5L, 1L, "12")

        val updatedSets = capturedExercises[0].sets
        assertEquals(12, updatedSets.first { it.id == 1L }.reps)
        assertEquals(8, updatedSets.first { it.id == 2L }.reps) // unchanged
    }

    @Test
    fun `updateSetReps does nothing when reps is not a valid int`() = runTest {
        val ex = exercise(5L, listOf(set(1L, 1)))
        val scribble = scribble(listOf(ex))

        useCase.updateSetReps(scribble, 5L, 1L, "not-a-number")

        coVerify(exactly = 0) { updateExerciseUseCase(any()) }
    }

    // deleteSet tests
    @Test
    fun `deleteSet removes correct set from exercise and calls removeSetUseCase`() = runTest {
        val sets = listOf(set(1L, 1), set(2L, 2))
        val ex = exercise(5L, sets)
        val scribble = scribble(listOf(ex))
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase.deleteSet(scribble, 5L, 1L)

        coVerify(exactly = 1) { removeSetUseCase(1L) }
        assertEquals(1, capturedExercises[0].sets.size)
        assertEquals(2L, capturedExercises[0].sets[0].id)
    }

    @Test
    fun `deleteSet does nothing when exercise not found`() = runTest {
        val scribble = scribble(listOf(exercise(5L)))

        useCase.deleteSet(scribble, 99L, 1L)

        coVerify(exactly = 0) { removeSetUseCase(any()) }
        coVerify(exactly = 0) { updateExerciseUseCase(any()) }
    }
}
