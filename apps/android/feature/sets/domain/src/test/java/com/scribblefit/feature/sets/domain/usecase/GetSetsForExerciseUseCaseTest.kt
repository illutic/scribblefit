package com.scribblefit.feature.sets.domain.usecase

import app.cash.turbine.test
import com.scribblefit.core.model.Set
import com.scribblefit.feature.sets.domain.SetRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetSetsForExerciseUseCaseTest {

    private val repository = mockk<SetRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetSetsForExerciseUseCase(repository, testDispatcher)

    @Test
    fun `should return flow of sets from repository`() = runTest(testDispatcher) {
        // Given
        val workoutExerciseId = 1L
        val sets = listOf(Set(101L, 1, 100f, 10))
        every { repository.getSetsForExercise(workoutExerciseId) } returns flowOf(sets)

        // When
        val result = useCase(workoutExerciseId)

        // Then
        assertTrue(result.isSuccess)
        result.getOrThrow().test {
            assertEquals(sets, awaitItem())
            awaitComplete()
        }
    }
}
