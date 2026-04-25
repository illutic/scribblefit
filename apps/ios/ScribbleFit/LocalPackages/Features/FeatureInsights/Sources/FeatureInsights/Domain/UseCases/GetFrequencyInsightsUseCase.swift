import Foundation
import CoreModel

@MainActor
public final class GetFrequencyInsightsUseCase {
    private let scribbleRepository: ScribbleRepository

    public init(scribbleRepository: ScribbleRepository) {
        self.scribbleRepository = scribbleRepository
    }

    public func execute(startDate: Date, endDate: Date) -> AsyncStream<FrequencyData> {
        let stream = scribbleRepository.observeScribbles(startDate: startDate, endDate: endDate)

        return AsyncStream { continuation in
            Task {
                for await scribbles in stream {
                    let completedScribbles = scribbles.filter { $0.status == .completed }
                    let data = calculateFrequency(scribbles: completedScribbles, startDate: startDate, endDate: endDate)
                    continuation.yield(data)
                }
            }
        }
    }

    private func calculateFrequency(scribbles: [Scribble], startDate: Date, endDate: Date) -> FrequencyData {
        let totalWorkouts = scribbles.count
        let totalExercises = scribbles.reduce(0) { $0 + $1.exercises.count }
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
