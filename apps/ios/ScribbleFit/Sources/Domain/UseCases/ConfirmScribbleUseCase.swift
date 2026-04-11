import Foundation

@MainActor
public final class ConfirmScribbleUseCase {
    private let scribbleRepository: ScribbleRepository
    private let workoutRepository: WorkoutRepository

    public init(scribbleRepository: ScribbleRepository, workoutRepository: WorkoutRepository) {
        self.scribbleRepository = scribbleRepository
        self.workoutRepository = workoutRepository
    }

    public func execute(scribble: Scribble) async throws {
        guard scribble.status == .success else {
            throw ConfirmScribbleError.invalidStatus(scribble.status)
        }

        let workout = Workout(
            id: UUID(),
            date: scribble.createdAt,
            exercises: scribble.exercises,
            notes: ["Imported from scribble: \(scribble.rawText)"]
        )

        try await workoutRepository.saveWorkout(workout)
        
        var completedScribble = scribble
        completedScribble.status = .completed
        try await scribbleRepository.updateScribble(completedScribble)
    }
}

public enum ConfirmScribbleError: Error {
    case invalidStatus(ScribbleStatus)
}
