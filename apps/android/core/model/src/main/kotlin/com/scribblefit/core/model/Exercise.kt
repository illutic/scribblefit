package com.scribblefit.core.model

data class Exercise(
    val id: Long,
    val canonicalName: String,
    val muscleGroup: String,
    val sets: List<Set>,
    val isDraft: Boolean = false,
    val estimated1RM: Float? = null,
    val intensity: Float? = null,
    val improvement: Float? = null,
)

/**
 * Returns a copy of this [Exercise] with [estimated1RM] and [intensity] calculated
 * from its sets using the Epley formula.
 *
 * - **1RM (Epley)**: `weight * (1 + reps / 30)`  — evaluated on the set that yields the highest 1RM.
 * - **Intensity**: `heaviest weight used / estimated1RM` (0-1 range).
 *
 * If there are no sets, or every set has zero weight, the exercise is returned unchanged.
 */
fun Exercise.withCalculatedStats(): Exercise {
    if (sets.isEmpty()) return this

    val heaviestSet = sets.maxByOrNull { it.weight * (1 + it.reps / 30f) } ?: return this
    val oneRm = heaviestSet.weight * (1 + heaviestSet.reps / 30f)
    if (oneRm <= 0f) return this

    val maxWeight = sets.maxOf { it.weight }
    val intensityValue = if (oneRm > 0f) maxWeight / oneRm else null

    return copy(
        estimated1RM = oneRm,
        intensity = intensityValue,
    )
}
