import Foundation
import CoreModel

public struct ReorderSetsUseCase: Sendable {
    public init() {}
    
    public func execute(sets: [ExerciseSet]) -> [ExerciseSet] {
        return sets.enumerated().map { index, set in
            ExerciseSet(
                id: set.id,
                setNumber: index + 1,
                weight: set.weight,
                reps: set.reps,
                rpe: set.rpe,
                notes: set.notes
            )
        }
    }
}
