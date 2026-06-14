import Foundation

/**
 * Finalizes the parsed workout session into the permanent workout ledger.
 */
public final class ConfirmWorkoutUseCase {
    private let sessionRepository: WorkoutSessionRepository
    private let ledgerRepository: LedgerRepository

    public init(sessionRepository: WorkoutSessionRepository, ledgerRepository: LedgerRepository) {
        self.sessionRepository = sessionRepository
        self.ledgerRepository = ledgerRepository
    }

    public func execute(workout: ParsedWorkout) async throws {
        let totalVolume = workout.exercises.reduce(0.0) { (acc, exercise) in
            acc + exercise.sets.reduce(0.0) { (setAcc, set) in
                setAcc + (set.weight * Double(set.reps))
            }
        }

        let workoutHistory = WorkoutHistory(
            id: UUID().uuidString,
            date: Date(),
            location: workout.location,
            totalVolume: totalVolume,
            exercises: workout.exercises.map { exercise in
                ExerciseHistory(
                    canonicalName: exercise.canonicalName,
                    sets: exercise.sets.map { set in
                        SetHistory(weight: set.weight, reps: set.reps, rpe: set.rpe, notes: set.notes)
                    }
                )
            }
        )

        try await ledgerRepository.logWorkout(workoutHistory)
        try await sessionRepository.clearActiveSession()
    }
}
