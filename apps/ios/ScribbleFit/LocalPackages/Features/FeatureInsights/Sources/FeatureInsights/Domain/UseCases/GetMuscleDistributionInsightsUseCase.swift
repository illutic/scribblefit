import Foundation
import CoreModel

@MainActor
public final class GetMuscleDistributionInsightsUseCase {
    private let workoutRepository: WorkoutRepository

    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[MuscleGroupDistribution]> {
        let stream = workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
        return AsyncStream { continuation in
            Task {
                for await workouts in stream {
                    let data = calculateDistribution(workouts: workouts)
                    continuation.yield(data)
                }
            }
        }
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
