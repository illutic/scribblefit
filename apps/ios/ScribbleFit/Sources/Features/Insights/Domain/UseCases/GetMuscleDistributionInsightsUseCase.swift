import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class GetMuscleDistributionInsightsUseCase {
    private let workoutRepository: WorkoutRepository

    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }

    public func execute(startDate: Date, endDate: Date) async throws -> [MuscleGroupDistribution] {
        let workouts = try await workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
        return calculateDistribution(workouts: workouts)
    }

    private func calculateDistribution(workouts: [Workout]) -> [MuscleGroupDistribution] {
        var setsByGroup: [String: Int] = [:]
        var totalSets = 0

        for workout in workouts {
            for exercise in workout.exercises {
                let group = exercise.muscleGroup.isEmpty
                    ? String(localized: "Other")
                    : exercise.muscleGroup
                let setCount = exercise.sets.count
                setsByGroup[group, default: 0] += setCount
                totalSets += setCount
            }
        }

        guard totalSets > 0 else { return [] }

        return setsByGroup
            .map { group, count in
                MuscleGroupDistribution(
                    muscleGroup: group,
                    percentage: (Float(count) / Float(totalSets)) * 100.0
                )
            }
            .sorted { $0.percentage > $1.percentage }
    }
}
