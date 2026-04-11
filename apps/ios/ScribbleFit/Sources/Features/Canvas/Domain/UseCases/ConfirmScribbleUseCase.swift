import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

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

        // Deep-copy exercises with fresh UUIDs to avoid SwiftData unique
        // constraint conflicts with the scribble's exercise entities.
        let freshExercises = scribble.exercises.map { exercise in
            Exercise(
                id: UUID(),
                canonicalName: exercise.canonicalName,
                muscleGroup: exercise.muscleGroup,
                sets: exercise.sets.map { set in
                    ExerciseSet(
                        id: UUID(),
                        setNumber: set.setNumber,
                        weight: set.weight,
                        reps: set.reps,
                        rpe: set.rpe,
                        notes: set.notes
                    )
                },
                isDraft: false,
                estimated1RM: exercise.estimated1RM,
                intensity: exercise.intensity,
                improvement: exercise.improvement
            )
        }

        let workout = Workout(
            id: UUID(),
            date: scribble.createdAt,
            exercises: freshExercises,
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
