import Foundation
import CoreModel

/**
 * Creates a new scribble from manual exercise input.
 * The scribble is created in SUCCESS status so it appears immediately on the canvas as a parsed entry.
 */
public struct CreateManualScribbleUseCase: Sendable {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    @MainActor
    public func execute(
        exerciseName: String,
        muscleGroup: String,
        sets: [ExerciseSet],
        date: Date
    ) async throws {
        let exercise = Exercise(
            id: UUID(),
            canonicalName: exerciseName,
            muscleGroup: muscleGroup,
            sets: sets,
            createdAt: date
        )

        let scribble = Scribble(
            id: UUID(),
            rawText: "Manual Entry: \(exerciseName)",
            status: .success,
            createdAt: date,
            exercises: [exercise]
        )

        try await scribbleRepository.addScribble(scribble)
    }
}
