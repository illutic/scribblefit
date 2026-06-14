package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.model.Set
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReorderSetsUseCaseTest {

    private lateinit var useCase: ReorderSetsUseCase

    private fun set(id: Long, setNumber: Int) =
        Set(id = id, setNumber = setNumber, reps = 10, weight = 80f)

    @Before
    fun setup() {
        useCase = ReorderSetsUseCase()
    }

    @Test
    fun `invoke returns empty list when given empty list`() {
        val result = useCase(emptyList())

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke reassigns setNumbers starting from 1`() {
        val sets = listOf(set(1L, 5), set(2L, 3), set(3L, 7))

        val result = useCase(sets)

        assertEquals(1, result[0].setNumber)
        assertEquals(2, result[1].setNumber)
        assertEquals(3, result[2].setNumber)
    }

    @Test
    fun `invoke preserves order of sets`() {
        val sets = listOf(set(10L, 3), set(20L, 1), set(30L, 5))

        val result = useCase(sets)

        assertEquals(10L, result[0].id)
        assertEquals(20L, result[1].id)
        assertEquals(30L, result[2].id)
    }

    @Test
    fun `invoke preserves other set fields`() {
        val originalSet = Set(id = 5L, setNumber = 10, reps = 12, weight = 60f, rpe = 7f, notes = "hard")
        val sets = listOf(originalSet)

        val result = useCase(sets)

        assertEquals(1, result[0].setNumber) // renumbered
        assertEquals(12, result[0].reps)
        assertEquals(60f, result[0].weight)
        assertEquals(7f, result[0].rpe)
        assertEquals("hard", result[0].notes)
    }

    @Test
    fun `invoke handles single set`() {
        val sets = listOf(set(1L, 99))

        val result = useCase(sets)

        assertEquals(1, result[0].setNumber)
    }

    @Test
    fun `invoke renumbers correctly after deletion (gap in numbers)`() {
        // Simulate sets after a middle set was deleted: 1, 3 → should become 1, 2
        val sets = listOf(set(1L, 1), set(3L, 3))

        val result = useCase(sets)

        assertEquals(1, result[0].setNumber)
        assertEquals(2, result[1].setNumber)
    }

    @Test
    fun `invoke output size matches input size`() {
        val sets = (1..5).map { set(it.toLong(), it * 2) }

        val result = useCase(sets)

        assertEquals(5, result.size)
    }
}
