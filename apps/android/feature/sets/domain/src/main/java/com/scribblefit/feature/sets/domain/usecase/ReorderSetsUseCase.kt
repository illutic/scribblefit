package com.scribblefit.feature.sets.domain.usecase

import com.scribblefit.core.model.Set

class ReorderSetsUseCase {
    operator fun invoke(sets: List<Set>): List<Set> {
        return sets.mapIndexed { index, set ->
            set.copy(setNumber = index + 1)
        }
    }
}
