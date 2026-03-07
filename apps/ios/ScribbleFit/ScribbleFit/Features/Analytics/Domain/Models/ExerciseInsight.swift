import Foundation

public struct ExerciseInsight: Codable, Sendable {
    public let exerciseId: String
    public let estimated1RM: Double
    public let prDetected: Bool
    public let trendDirection: InsightTrend
    public let breakdownText: String
    public let timestamp: Date
    
    public init(
        exerciseId: String,
        estimated1RM: Double,
        prDetected: Bool,
        trendDirection: InsightTrend,
        breakdownText: String,
        timestamp: Date
    ) {
        self.exerciseId = exerciseId
        self.estimated1RM = estimated1RM
        self.prDetected = prDetected
        self.trendDirection = trendDirection
        self.breakdownText = breakdownText
        self.timestamp = timestamp
    }
}

public enum InsightTrend: String, Codable, Sendable {
    case improving
    case stable
    case plateaued
    case declining
}
