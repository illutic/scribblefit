import Foundation
import CoreModel

/**
 * Manually adds an exercise with sets to a workout.
 */
public struct AddManualExerciseUseCase: Sendable {
    private let exerciseRepository: ExerciseRepository

    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
    }

    @MainActor
    public func execute(
        workoutId: UUID,
        exerciseName: String,
        muscleGroup: String,
        sets: [ExerciseSet]
    ) async throws {
        let exercise = Exercise(
            id: UUID(),
            canonicalName: exerciseName,
            muscleGroup: muscleGroup,
            sets: sets
        )

        try await exerciseRepository.saveExercise(exercise, to: workoutId)
    }
}
