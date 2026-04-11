import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class GetFrequencyInsightsUseCase {
    private let workoutRepository: WorkoutRepository

    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }

    public func execute(startDate: Date, endDate: Date) async throws -> FrequencyData {
        let workouts = try await workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
        return calculateFrequency(workouts: workouts, startDate: startDate, endDate: endDate)
    }

    private func calculateFrequency(workouts: [Workout], startDate: Date, endDate: Date) -> FrequencyData {
        let totalWorkouts = workouts.count
        let totalExercises = workouts.reduce(0) { $0 + $1.exercises.count }
        let daysBetween = max(Calendar.current.dateComponents([.day], from: startDate, to: endDate).day ?? 1, 1)
        let weeks = max(Float(daysBetween) / 7.0, 1.0)
        let workoutsPerWeek = Float(totalWorkouts) / weeks

        return FrequencyData(
            totalWorkouts: totalWorkouts,
            workoutsPerWeek: workoutsPerWeek,
            totalExercises: totalExercises
        )
    }
}
