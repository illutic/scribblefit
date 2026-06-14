import Foundation
import CoreModel
import CoreCommon

@MainActor
public final class CalculateTrendsUseCase {
    private let exerciseRepository: ExerciseRepository

    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
    }

    public func execute(exercise: Exercise) async -> ExerciseTrends? {
        do {
            let exercises = try await exerciseRepository.getExercises(query: exercise.canonicalName)
                .filter { $0.canonicalName.lowercased() == exercise.canonicalName.lowercased() }

            if exercises.isEmpty { return nil }

            let sortedExercises = exercises.sorted(by: { $0.createdAt < $1.createdAt })

            // Weekly Stats (last 7 days)
            let calendar = Calendar.current
            let now = Date()
            let sevenDaysAgo = calendar.date(byAdding: .day, value: -7, to: now)!

            let thisWeekExercises = exercises.filter { $0.createdAt >= sevenDaysAgo }

            let maxWeightThisWeek = thisWeekExercises.flatMap { $0.sets }
                .compactMap { $0.weight }.max() ?? Float(0.0)

            // Current 1RM
            let current1RM = exercise.sets.map {
                Calculations.calculate1RM(weight: $0.weight ?? Float(0.0), reps: $0.reps)
            }.max() ?? Float(0.0)

            // Previous Max 1RM (all-time except current session)
            let previousMax = sortedExercises.filter { $0.id != exercise.id }.flatMap { session in
                session.sets.map {
                    Calculations.calculate1RM(weight: $0.weight ?? Float(0.0), reps: $0.reps)
                }
            }.max() ?? Float(0.0)

            let improvement = previousMax > 0 ? (current1RM - previousMax) / previousMax : Float(0.0)

            let trendDirection: TrendDirection = {
                let threshold: Float = 0.05
                if improvement > threshold { return .improving }
                if improvement < -threshold { return .declining }
                return .stable
            }()

            let intensity = maxWeightThisWeek > 0 ? current1RM / maxWeightThisWeek : Float(0.0)

            let lastVolume = exercise.sets.reduce(Float(0.0)) { $0 + ($1.weight ?? Float(0.0)) * Float($1.reps) }

            // Previous Session Volume
            let previousSession = sortedExercises.last(where: { $0.id != exercise.id })
            let previousVolume = previousSession?.sets.reduce(Float(0.0)) { $0 + ($1.weight ?? Float(0.0)) * Float($1.reps) } ?? Float(0.0)

            let lastVolumeTrend: TrendDirection = {
                if lastVolume > previousVolume { return .improving }
                if lastVolume < previousVolume { return .declining }
                return .stable
            }()

            return ExerciseTrends(
                current1RM: current1RM,
                trendDirection: trendDirection,
                lastVolume: lastVolume,
                lastVolumeTrend: lastVolumeTrend,
                intensity: intensity,
                improvement: improvement
            )
        } catch {
            return nil
        }
    }
}
