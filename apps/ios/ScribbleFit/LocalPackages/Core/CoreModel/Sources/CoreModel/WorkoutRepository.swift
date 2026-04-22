import Foundation

@MainActor
public protocol WorkoutRepository: Sendable {
    func getWorkout(id: UUID) async throws -> Workout?
    func saveWorkout(_ workout: Workout) async throws
    func deleteWorkout(id: UUID) async throws
    func observeWorkout(id: UUID) -> AsyncStream<Workout?>
    func getWorkouts(for date: Date) -> AsyncStream<[Workout]>
    func getWorkoutsInRange(startDate: Date, endDate: Date) -> AsyncStream<[Workout]>
    func getWorkoutsWithExercise(exerciseName: String) -> AsyncStream<[Workout]>
}
