import Foundation
import CoreModel

@MainActor
public final class GetMuscleDistributionInsightsUseCase {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<[MuscleGroupDistribution]> {
        let stream = scribbleRepository.observeScribbles(startDate: startDate, endDate: endDate)

        return AsyncStream { continuation in
            Task {
                for await scribbles in stream {
                    let completedScribbles = scribbles.filter { $0.status == .completed }
                    let data = calculateDistribution(scribbles: completedScribbles)
                    continuation.yield(data)
                }
            }
        }
    }

    private func calculateDistribution(scribbles: [Scribble]) -> [MuscleGroupDistribution] {
        var setsByGroup: [String: Int] = [:]
        var totalSets = 0

        for scribble in scribbles {
            for exercise in scribble.exercises {
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
