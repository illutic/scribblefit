package com.scribblefit.core.common

object Calculations {
    /**
     * Epley formula: Weight × (1 + Reps/30)
     */
    fun calculate1RM(weight: Float, reps: Int): Float {
        if (reps <= 0) return 0f
        if (reps == 1) return weight
        return weight * (1f + reps.toFloat() / 30f)
    }

    fun calculateVolume(weight: Float?, reps: Int): Float {
        return (weight ?: 0f) * reps
    }
}
