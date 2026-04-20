import Foundation
import CoreModel

@MainActor
public final class GetVolumeInsightsUseCase {
    private let workoutRepository: WorkoutRepository

    public init(workoutRepository: WorkoutRepository) {
        self.workoutRepository = workoutRepository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[VolumeDataPoint]> {
        let stream = workoutRepository.getWorkoutsInRange(startDate: startDate, endDate: endDate)
        return AsyncStream { continuation in
            Task {
                for await workouts in stream {
                    let points = workouts.compactMap { workout -> VolumeDataPoint? in
                        let totalVolume = workout.exercises.reduce(0) { workoutVol, exercise in
                            workoutVol + exercise.sets.reduce(0) { exerciseVol, set in
                                exerciseVol + (set.weight ?? 0) * Float(set.reps)
                            }
                        }
                        guard totalVolume > 0 else { return nil }
                        return VolumeDataPoint(date: workout.date, volume: totalVolume)
                    }
                    continuation.yield(points)
                }
            }
        }
    }
}
