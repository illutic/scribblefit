import Foundation

public final class ConfirmWorkoutUseCase: Sendable {
    private let scribbleRepository: any ScribbleRepository
    private let ledgerRepository: any LedgerRepository

    public init(
        scribbleRepository: any ScribbleRepository,
        ledgerRepository: any LedgerRepository
    ) {
        self.scribbleRepository = scribbleRepository
        self.ledgerRepository = ledgerRepository
    }

    public func execute(parsedWorkout: ParsedWorkout, scribbleId: String) async throws {
        let isoFormatter = ISO8601DateFormatter()
        isoFormatter.formatOptions = [.withFullDate]
        let date = isoFormatter.date(from: parsedWorkout.date) ?? Date()
        let totalVolume = parsedWorkout.exercises.reduce(0.0) { sum, ex in
            sum + ex.sets.reduce(0.0) { $0 + $1.weight * Double($1.reps) }
        }
        let history = WorkoutHistory(
            id: scribbleId,
            date: date,
            location: parsedWorkout.location,
            totalVolume: totalVolume,
            exercises: parsedWorkout.exercises.map { ex in
                ExerciseHistory(
                    canonicalName: ex.canonicalName,
                    sets: ex.sets.map { s in
                        SetHistory(weight: s.weight, reps: s.reps, rpe: s.rpe, notes: s.notes)
                    }
                )
            }
        )
        try await ledgerRepository.logWorkout(history)
        try await scribbleRepository.updateSyncStatus(id: scribbleId, status: .completed)
    }
}
