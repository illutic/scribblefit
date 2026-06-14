import Foundation
import CoreModel

/**
 * Appends a new set to an exercise.
 */
public struct AddSetToExerciseUseCase: Sendable {
    private let exerciseRepository: ExerciseRepository

    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
    }

    @MainActor
    public func execute(exerciseId: UUID, nextSetNumber: Int) async throws {
        guard let exercise = try await exerciseRepository.getExercise(id: exerciseId) else {
            return
        }

        let newSet = ExerciseSet(
            id: UUID(),
            setNumber: nextSetNumber,
            weight: 0.0,
            reps: 0
        )

        var updatedSets = exercise.sets
        updatedSets.append(newSet)

        try await exerciseRepository.updateExercise(exercise.copy(sets: updatedSets))
    }
}
