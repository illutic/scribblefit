import Foundation

public protocol LedgerRepository: Sendable {
    func getWorkoutHistory() async throws -> [WorkoutHistory]
}
