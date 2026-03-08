import Foundation

public protocol WorkoutSessionRepository: Sendable {
    func getActiveSession() async throws -> WorkoutSession?
    func updateSession(_ session: WorkoutSession) async throws
    func clearActiveSession() async throws
}
