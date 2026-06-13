import Foundation
import CoreModel
import CoreCommon

/**
 * Time periods for filtering trend data.
 */
public enum TrendPeriod: String, Sendable, Codable {
    case oneMonth = "1M"
    case threeMonths = "3M"
    case sixMonths = "6M"
    case oneYear = "1Y"
    case all = "ALL"
}

/**
 * Data point for a trend chart.
 */
public struct TrendDataPoint: Sendable, Identifiable {
    public let id = UUID()
    public let date: Date
    public let value: Float
}

/**
 * Insights calculated from trend data.
 */
public struct TrendInsights: Sendable {
    public let personalBest: Float
    public let percentageChange: Float
    public let trendDirection: TrendDirection
}

/**
 * Trend data for a specific metric.
 */
public struct MetricTrendData: Sendable {
    public let dataPoints: [TrendDataPoint]
    public let insights: TrendInsights
}

/**
 * Result model containing all data for the trends screen.
 */
public struct ExerciseTrendResult: Sendable {
    public let oneRM: MetricTrendData
    public let volume: MetricTrendData
}

/**
 * Use case to fetch and calculate trend data for an exercise.
 */
@MainActor
public final class GetExerciseTrendDataUseCase {
    private let exerciseRepository: ExerciseRepository
    
    public init(exerciseRepository: ExerciseRepository) {
        self.exerciseRepository = exerciseRepository
    }
    
    public func execute(
        exerciseName: String,
        period: TrendPeriod
    ) -> AsyncStream<ExerciseTrendResult> {
        AsyncStream { continuation in
            let exercisesStream = exerciseRepository.observeExercises(query: exerciseName)
            
            let task = Task {
                for await exercises in exercisesStream {
                    let allHistory = exercises
                        .filter { $0.canonicalName.lowercased() == exerciseName.lowercased() }
                        .sorted(by: { $0.createdAt < $1.createdAt })
                    
                    if allHistory.isEmpty {
                        let emptyData = MetricTrendData(
                            dataPoints: [],
                            insights: TrendInsights(personalBest: 0, percentageChange: 0, trendDirection: .stable)
                        )
                        continuation.yield(ExerciseTrendResult(
                            oneRM: emptyData,
                            volume: emptyData
                        ))
                        continue
                    }
                    
                    let filteredHistory = filterByPeriod(allHistory, period: period)
                    
                    let oneRMData = calculateMetricData(filteredHistory) { exercise in
                        exercise.sets.map { Calculations.calculate1RM(weight: $0.weight ?? 0, reps: $0.reps) }.max() ?? 0
                    }
                    
                    let volumeData = calculateMetricData(filteredHistory) { exercise in
                        exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
                    }
                    
                    continuation.yield(ExerciseTrendResult(
                        oneRM: oneRMData,
                        volume: volumeData
                    ))
                }
            }
            
            continuation.onTermination = { _ in
                task.cancel()
            }
        }
    }
    
    private func calculateMetricData(
        _ history: [Exercise],
        valueExtractor: (Exercise) -> Float
    ) -> MetricTrendData {
        let dataPoints = history.map { exercise in
            TrendDataPoint(date: exercise.createdAt, value: valueExtractor(exercise))
        }
        
        let personalBest = dataPoints.map { $0.value }.max() ?? 0
        
        let percentageChange: Float = {
            guard dataPoints.count >= 2, let first = dataPoints.first?.value, first > 0 else { return 0 }
            let last = dataPoints.last?.value ?? 0
            return ((last - first) / first) * 100
        }()
        
        let direction: TrendDirection = {
            if percentageChange > 5 { return .improving }
            if percentageChange < -5 { return .declining }
            return .stable
        }()
        
        return MetricTrendData(
            dataPoints: dataPoints,
            insights: TrendInsights(personalBest: personalBest, percentageChange: percentageChange, trendDirection: direction)
        )
    }
    
    private func filterByPeriod(_ history: [Exercise], period: TrendPeriod) -> [Exercise] {
        if period == .all { return history }
        
        let calendar = Calendar.current
        let now = Date()
        let startDate: Date? = {
            switch period {
            case .oneMonth: return calendar.date(byAdding: .month, value: -1, to: now)
            case .threeMonths: return calendar.date(byAdding: .month, value: -3, to: now)
            case .sixMonths: return calendar.date(byAdding: .month, value: -6, to: now)
            case .oneYear: return calendar.date(byAdding: .year, value: -1, to: now)
            case .all: return nil
            }
        }()
        
        guard let start = startDate else { return history }
        return history.filter { $0.createdAt >= start }
    }
}
