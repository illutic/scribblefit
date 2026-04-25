import Foundation
import CoreModel

@MainActor
public final class GetVolumeInsightsUseCase {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[VolumeDataPoint]> {
        let stream = scribbleRepository.observeScribbles(startDate: startDate, endDate: endDate)

        return AsyncStream { continuation in
            Task {
                for await scribbles in stream {
                    let completedScribbles = scribbles.filter { $0.status == .completed }
                    let points = completedScribbles.compactMap { scribble -> VolumeDataPoint? in
                        let totalVolume = scribble.exercises.reduce(0) { workoutVol, exercise in
                            workoutVol + exercise.sets.reduce(0) { exerciseVol, set in
                                exerciseVol + (set.weight ?? 0) * Float(set.reps)
                            }
                        }
                        guard totalVolume > 0 else { return nil }
                        return VolumeDataPoint(date: scribble.createdAt, volume: totalVolume)
                    }
                    continuation.yield(points)
                }
            }
        }
    }
}
