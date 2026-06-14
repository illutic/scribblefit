package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MarkExerciseAsCompleteUseCaseTest {

    private val updateExerciseUseCase: UpdateExerciseUseCase = mockk()
    private val testDispatcher = StandardTestDispatcher()
    private lateinit var useCase: MarkExerciseAsCompleteUseCase

    private val draftExercise = Exercise(
        id = 1L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = listOf(Set(0, 1, 10, 100f)), createdAt = 1_000_000L, isDraft = true
    )

    @Before
    fun setup() {
        useCase = MarkExerciseAsCompleteUseCase(
            updateExerciseUseCase = updateExerciseUseCase,
            coroutineDispatcher = testDispatcher
        )
    }

    @Test
    fun `invoke returns success when update succeeds`() = runTest(testDispatcher) {
        coEvery { updateExerciseUseCase(any()) } returns Result.success(Unit)

        val result = useCase(draftExercise)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke calls updateExerciseUseCase with isDraft set to false`() = runTest(testDispatcher) {
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase(draftExercise)

        assertTrue(capturedExercises[0].isDraft == false)
    }

    @Test
    fun `invoke preserves all other exercise fields`() = runTest(testDispatcher) {
        val capturedExercises = mutableListOf<Exercise>()
        coEvery { updateExerciseUseCase(capture(capturedExercises)) } returns Result.success(Unit)

        useCase(draftExercise)

        val captured = capturedExercises[0]
        assertTrue(captured.id == draftExercise.id)
        assertTrue(captured.canonicalName == draftExercise.canonicalName)
        assertTrue(captured.sets == draftExercise.sets)
    }

    @Test
    fun `invoke propagates failure from updateExerciseUseCase`() = runTest(testDispatcher) {
        coEvery { updateExerciseUseCase(any()) } returns Result.failure(RuntimeException("DB error"))

        val result = useCase(draftExercise)

        assertFalse(result.isSuccess)
    }

    @Test
    fun `invoke works on exercise that is already not a draft`() = runTest(testDispatcher) {
        val nonDraftExercise = draftExercise.copy(isDraft = false)
        coEvery { updateExerciseUseCase(any()) } returns Result.success(Unit)

        val result = useCase(nonDraftExercise)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { updateExerciseUseCase(nonDraftExercise) }
    }
}
