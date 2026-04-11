import Foundation
#if SWIFT_PACKAGE
import CoreModel
#endif

@MainActor
public final class GetVolumeInsightsUseCase {
    private let workoutRepository: WorkoutRepository

    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }

    public func execute(startDate: Date, endDate: Date) async throws -> [VolumeDataPoint] {
        let workouts = try await workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
        return calculateVolumePerDate(workouts: workouts)
    }

    private func calculateVolumePerDate(workouts: [Workout]) -> [VolumeDataPoint] {
        let calendar = Calendar.current
        var volumeByDate: [Date: Float] = [:]

        for workout in workouts {
            let dayStart = calendar.startOfDay(for: workout.date)
            var volume: Float = 0
            for exercise in workout.exercises {
                for set in exercise.sets {
                    volume += set.weight * Float(set.reps)
                }
            }
            volumeByDate[dayStart, default: 0] += volume
        }

        return volumeByDate
            .map { VolumeDataPoint(date: $0.key, volume: $0.value) }
            .sorted { $0.date < $1.date }
    }
}
