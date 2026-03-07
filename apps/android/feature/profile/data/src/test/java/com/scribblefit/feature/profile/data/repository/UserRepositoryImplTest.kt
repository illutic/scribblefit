package com.scribblefit.feature.profile.data.repository

import com.scribblefit.feature.ledger.domain.model.WorkoutHistory
import com.scribblefit.feature.ledger.domain.repository.LedgerRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {

    private lateinit var ledgerRepository: LedgerRepository
    private lateinit var repository: UserRepositoryImpl

    @Before
    fun setup() {
        ledgerRepository = mockk()
        repository = UserRepositoryImpl(ledgerRepository)
    }

    @Test
    fun `getUserStats aggregates ledger data correctly`() = runTest {
        // Given
        val history = listOf(
            WorkoutHistory("1", 1000L, null, 500.0, emptyList()),
            WorkoutHistory("2", 2000L, null, 1000.0, emptyList())
        )
        every { ledgerRepository.getWorkoutHistory() } returns flowOf(history)

        // When
        val stats = repository.getUserStats().first()

        // Then
        assertEquals(2, stats.totalWorkouts)
        assertEquals(1500.0, stats.lifetimeVolume, 0.1)
        assertEquals(1000L, stats.joinDate)
    }

    @Test
    fun `getUserStats handles empty history`() = runTest {
        // Given
        every { ledgerRepository.getWorkoutHistory() } returns flowOf(emptyList())

        // When
        val stats = repository.getUserStats().first()

        // Then
        assertEquals(0, stats.totalWorkouts)
        assertEquals(0.0, stats.lifetimeVolume, 0.1)
    }
}
