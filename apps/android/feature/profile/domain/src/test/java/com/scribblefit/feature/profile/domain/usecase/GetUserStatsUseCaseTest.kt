package com.scribblefit.feature.profile.domain.usecase

import com.scribblefit.feature.profile.domain.model.UserStats
import com.scribblefit.feature.profile.domain.repository.UserRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetUserStatsUseCaseTest {

    private lateinit var repository: UserRepository
    private lateinit var useCase: GetUserStatsUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetUserStatsUseCase(repository)
    }

    @Test
    fun `usecase delegates to repository correctly`() = runTest {
        // Given
        val stats = UserStats(10, 1000.0, 5, 1000L)
        every { repository.getUserStats() } returns flowOf(stats)

        // When
        val result = useCase().first()

        // Then
        assertEquals(stats, result)
    }
}
