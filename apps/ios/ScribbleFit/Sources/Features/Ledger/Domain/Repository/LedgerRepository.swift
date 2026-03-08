import Foundation

public protocol LedgerRepository: Sendable {
    func getWorkoutHistory() async throws -> [WorkoutHistory]
    func logWorkout(_ workout: WorkoutHistory) async throws
}
