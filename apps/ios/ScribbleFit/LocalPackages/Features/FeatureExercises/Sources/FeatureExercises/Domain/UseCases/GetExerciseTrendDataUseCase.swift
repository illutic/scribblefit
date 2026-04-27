import Foundation
import CoreModel
import CoreCommon

/**
 * Metric types for trend visualization.
 */
public enum TrendMetric: String, Sendable, Codable {
    case oneRM = "ONE_RM"
    case volume = "VOLUME"
    case maxWeight = "MAX_WEIGHT"
}

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
 * Result model containing all data for the trends screen.
 */
public struct ExerciseTrendResult: Sendable {
    public let dataPoints: [TrendDataPoint]
    public let insights: TrendInsights
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
        metric: TrendMetric,
        period: TrendPeriod
    ) async throws -> ExerciseTrendResult {
        let allHistory = try await exerciseRepository.getExercises(query: exerciseName)
            .filter { $0.canonicalName.lowercased() == exerciseName.lowercased() }
            .sorted(by: { $0.createdAt < $1.createdAt })
        
        if allHistory.isEmpty {
            return ExerciseTrendResult(
                dataPoints: [],
                insights: TrendInsights(personalBest: 0, percentageChange: 0, trendDirection: .stable)
            )
        }
        
        let filteredHistory = filterByPeriod(allHistory, period: period)
        
        let dataPoints = filteredHistory.map { exercise in
            let value: Float = {
                switch metric {
                case .oneRM:
                    return exercise.sets.map { Calculations.calculate1RM(weight: $0.weight ?? 0, reps: $0.reps) }.max() ?? 0
                case .volume:
                    return exercise.sets.reduce(Float(0.0)) { $0 + Calculations.calculateVolume(weight: $1.weight, reps: $1.reps) }
                case .maxWeight:
                    return exercise.sets.compactMap { $0.weight }.max() ?? 0
                }
            }()
            return TrendDataPoint(date: exercise.createdAt, value: value)
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
        
        return ExerciseTrendResult(
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
