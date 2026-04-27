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
        notes: String,
        date: Date
    ) async throws {
        let exercise = Exercise(
            id: UUID(),
            canonicalName: exerciseName,
            muscleGroup: muscleGroup,
            sets: sets
        )
        
        let scribble = Scribble(
            id: UUID(),
            rawText: notes.isEmpty ? "Manual Entry: \(exerciseName)" : "Manual Entry: \(exerciseName)\nNotes: \(notes)",
            status: .success,
            createdAt: date,
            exercises: [exercise]
        )
        
        try await scribbleRepository.addScribble(scribble)
    }
}
