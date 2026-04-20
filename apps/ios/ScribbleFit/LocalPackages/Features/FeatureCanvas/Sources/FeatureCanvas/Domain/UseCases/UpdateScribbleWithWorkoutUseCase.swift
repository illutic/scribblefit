import Foundation
import CoreModel

@MainActor
public final class UpdateScribbleWithWorkoutUseCase: Sendable {
    private let scribbleRepository: ScribbleRepository
    private let workoutRepository: WorkoutRepository

    public init(scribbleRepository: ScribbleRepository, workoutRepository: WorkoutRepository) {
        self.scribbleRepository = scribbleRepository
        self.workoutRepository = workoutRepository
    }

    public func execute(scribble: Scribble) async throws {
        // 1. Update the scribble itself
        try await scribbleRepository.updateScribble(scribble)
        
        // 2. If it has an associated workout, update it as well
        if let workoutId = scribble.workoutId {
            if var workout = try await workoutRepository.getWorkout(id: workoutId) {
                // We sync the exercises from the scribble back to the workout.
                // Note: On iOS, we currently deep-copy during confirmation, 
                // but when editing, the user expects parity.
                // We keep the workout's metadata (date, notes) but update exercises.
                workout.exercises = scribble.exercises
                try await workoutRepository.saveWorkout(workout)
            }
        }
    }
}
