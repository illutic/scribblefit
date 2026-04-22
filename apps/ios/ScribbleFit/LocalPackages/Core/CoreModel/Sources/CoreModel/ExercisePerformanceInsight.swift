import Foundation

public struct ExercisePerformanceInsight: Sendable, Equatable {
    public let estimated1RM: Float
    public let prDetected: Bool
    public let trendDirection: TrendDirection
    public let breakdownText: String
    
    public init(estimated1RM: Float, prDetected: Bool, trendDirection: TrendDirection, breakdownText: String) {
        self.estimated1RM = estimated1RM
        self.prDetected = prDetected
        self.trendDirection = trendDirection
        self.breakdownText = breakdownText
    }
}
