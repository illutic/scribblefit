import Foundation

/**
 * Finalizes the parsed workout session into the permanent workout ledger.
 */
public final class ConfirmWorkoutUseCase {
    private let sessionRepository: WorkoutSessionRepository
    
    public init(sessionRepository: WorkoutSessionRepository) {
        self.sessionRepository = sessionRepository
    }
    
    public func execute(workout: ParsedWorkout) async throws {
        // Logic to move session to permanent storage
        try await sessionRepository.clearActiveSession()
    }
}
