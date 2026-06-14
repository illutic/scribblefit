package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise
import com.scribblefit.core.model.Set
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FormatExerciseSummaryUseCaseTest {

    private lateinit var useCase: FormatExerciseSummaryUseCase

    private fun exercise(vararg sets: Set) = Exercise(
        id = 1L, canonicalName = "Bench Press", muscleGroup = "Chest",
        sets = sets.toList(), createdAt = 1_000_000L
    )

    private fun set(weight: Float?, reps: Int, setNumber: Int = 1) =
        Set(id = 0, setNumber = setNumber, reps = reps, weight = weight)

    @Before
    fun setup() {
        useCase = FormatExerciseSummaryUseCase()
    }

    @Test
    fun `invoke returns empty string when exercise has no sets`() {
        val result = useCase(exercise(), Weight.KGS)

        assertEquals("", result)
    }

    @Test
    fun `invoke formats single uniform set in kg`() {
        val ex = exercise(set(100f, 10, 1), set(100f, 10, 2), set(100f, 10, 3))

        val result = useCase(ex, Weight.KGS)

        assertEquals("100.0kg • 3 sets x 10 reps", result)
    }

    @Test
    fun `invoke formats single uniform set in lb`() {
        val ex = exercise(set(225f, 5, 1), set(225f, 5, 2))

        val result = useCase(ex, Weight.LBS)

        assertEquals("225.0lb • 2 sets x 5 reps", result)
    }

    @Test
    fun `invoke formats varied sets`() {
        val ex = exercise(
            set(100f, 10, 1),
            set(100f, 10, 2),
            set(80f, 8, 3)
        )

        val result = useCase(ex, Weight.KGS)

        assertTrue(result.contains("100.0kg"))
        assertTrue(result.contains("80.0kg"))
    }

    @Test
    fun `invoke formats bodyweight exercises with null weight`() {
        val ex = exercise(set(null, 15, 1), set(null, 15, 2))

        val result = useCase(ex, Weight.KGS)

        assertTrue(result.contains("Bodyweight"))
    }

    @Test
    fun `invoke handles single set exercise`() {
        val ex = exercise(set(60f, 12, 1))

        val result = useCase(ex, Weight.KGS)

        assertEquals("60.0kg • 1 sets x 12 reps", result)
    }

    @Test
    fun `invoke groups consecutive identical sets correctly`() {
        val ex = exercise(
            set(100f, 10, 1),
            set(100f, 8, 2)  // different reps = different group
        )

        val result = useCase(ex, Weight.KGS)

        // Should show two separate groups since reps differ
        assertTrue(result.contains(","))
    }

    @Test
    fun `invoke formats varied reps correctly`() {
        val ex = exercise(
            set(100f, 5, 1),
            set(100f, 5, 2),
            set(100f, 5, 3)
        )

        val result = useCase(ex, Weight.KGS)

        assertEquals("100.0kg • 3 sets x 5 reps", result)
    }
}
