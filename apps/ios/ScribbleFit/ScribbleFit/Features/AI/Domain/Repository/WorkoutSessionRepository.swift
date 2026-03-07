import Foundation

public protocol WorkoutSessionRepository: Sendable {
    /**
     * Observes the current active, uncommitted workout session.
     */
    func getActiveSession() async throws -> WorkoutSession?

    /**
     * Updates or creates an active session.
     */
    func updateSession(_ session: WorkoutSession) async throws

    /**
     * Clears the active session after it has been committed to the ledger.
     */
    func clearActiveSession() async throws
}
