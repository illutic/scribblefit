package com.scribblefit.feature.scribble.domain.usecase

import app.cash.turbine.test
import com.scribblefit.core.model.CurrentDate
import com.scribblefit.core.model.Scribble
import com.scribblefit.core.model.ScribbleStatus
import com.scribblefit.feature.scribble.domain.ScribbleRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneOffset

class GetPendingScribblesByDateUseCaseTest {

    private val scribbleRepository = mockk<ScribbleRepository>()
    private val testDispatcher = StandardTestDispatcher()
    private val useCase = GetPendingScribblesByDateUseCase(scribbleRepository, testDispatcher)

    @Test
    fun `when called, should convert date to millis and return flow from repository`() =
        runTest(testDispatcher) {
            // Given
            val date = LocalDate.of(2026, 3, 14)
            val expectedMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val scribbles = listOf(
                Scribble(
                    id = 1L,
                    rawText = "text",
                    status = ScribbleStatus.PENDING,
                    createdAt = expectedMillis,
                    exercises = emptyList()
                )
            )
            every { scribbleRepository.getScribblesByDate(expectedMillis) } returns flowOf(
                scribbles
            )

            // When & Then
            useCase(CurrentDate(date)).test {
                assertEquals(scribbles, awaitItem())
                awaitComplete()
            }
        }
}
