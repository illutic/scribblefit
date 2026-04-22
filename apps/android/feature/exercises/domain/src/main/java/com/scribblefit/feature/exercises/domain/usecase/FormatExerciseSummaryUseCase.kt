package com.scribblefit.feature.exercises.domain.usecase

import com.scribblefit.core.config.domain.Weight
import com.scribblefit.core.model.Exercise

class FormatExerciseSummaryUseCase {
    operator fun invoke(exercise: Exercise, weightUnit: Weight): String {
        if (exercise.sets.isEmpty()) return ""

        val unitLabel = if (weightUnit == Weight.KGS) "kg" else "lb"

        // Group consecutive identical sets
        data class SetGroup(val weight: Float?, val reps: Int, var count: Int)
        val groups = mutableListOf<SetGroup>()

        for (set in exercise.sets) {
            val last = groups.lastOrNull()
            if (last != null && last.weight == set.weight && last.reps == set.reps) {
                last.count++
            } else {
                groups.add(SetGroup(set.weight, set.reps, 1))
            }
        }

        fun formatWeight(weight: Float?): String {
            return if (weight != null) {
                "%.1f%s".format(weight, unitLabel)
            } else {
                "Bodyweight"
            }
        }

        return if (groups.size == 1) {
            val group = groups[0]
            // Standard format for uniform sets: "100.0kg • 3 sets x 10 reps"
            "${formatWeight(group.weight)} • ${group.count} sets x ${group.reps} reps"
        } else {
            // Format for varied sets: "100.0kg 3x10, 90.0kg 2x8"
            groups.joinToString(", ") { group ->
                "${formatWeight(group.weight)} ${group.count}x${group.reps}"
            }
        }
    }
}
