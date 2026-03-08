import Foundation

public final class ConfirmWorkoutUseCase: Sendable {
    private let canvasRepository: any CanvasRepository
    private let sessionRepository: any WorkoutSessionRepository
    private let ledgerRepository: any LedgerRepository

    public init(
        canvasRepository: any CanvasRepository,
        sessionRepository: any WorkoutSessionRepository,
        ledgerRepository: any LedgerRepository
    ) {
        self.canvasRepository = canvasRepository
        self.sessionRepository = sessionRepository
        self.ledgerRepository = ledgerRepository
    }

    public func execute(confirmation: ConfirmationItem) async throws {
        let workout = confirmation.workout
        let isoFormatter = ISO8601DateFormatter()
        isoFormatter.formatOptions = [.withFullDate]
        let date = isoFormatter.date(from: workout.date) ?? Date()
        let totalVolume = workout.exercises.reduce(0.0) { sum, ex in
            sum + ex.sets.reduce(0.0) { $0 + $1.weight * Double($1.reps) }
        }
        let history = WorkoutHistory(
            id: confirmation.id,
            date: date,
            location: workout.location,
            totalVolume: totalVolume,
            exercises: workout.exercises.map { ex in
                ExerciseHistory(
                    canonicalName: ex.canonicalName,
                    sets: ex.sets.map { s in
                        SetHistory(weight: s.weight, reps: s.reps, rpe: s.rpe, notes: s.notes)
                    }
                )
            }
        )
        try await ledgerRepository.logWorkout(history)
        try await sessionRepository.clearActiveSession()
        try await canvasRepository.removeFeedItem(id: confirmation.id)
    }
}
