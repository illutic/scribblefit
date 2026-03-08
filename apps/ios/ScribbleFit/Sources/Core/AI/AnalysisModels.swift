import Foundation

public struct AnalysisSuggestion: Codable, Sendable {
    public let text: String
    public let emoji: String
    public let type: SuggestionType
    public let timestamp: Date
    public var fullText: String { "\(text) \(emoji)" }

    public init(text: String, emoji: String, type: SuggestionType, timestamp: Date = Date()) {
        self.text = text
        self.emoji = emoji
        self.type = type
        self.timestamp = timestamp
    }
}

public enum SuggestionType: String, Codable, Sendable {
    case recovery, pattern, milestone, rest
}

public struct AnalysisSummary: Codable, Sendable {
    public let period: SummaryPeriod
    public let summaryText: String
    public let highlights: [String]
    public let muscleDistribution: [MuscleGroupStat]
    public let focusArea: String
    public let volumeDelta: Double
    public let timestamp: Date

    public init(period: SummaryPeriod, summaryText: String, highlights: [String], muscleDistribution: [MuscleGroupStat], focusArea: String, volumeDelta: Double, timestamp: Date = Date()) {
        self.period = period
        self.summaryText = summaryText
        self.highlights = highlights
        self.muscleDistribution = muscleDistribution
        self.focusArea = focusArea
        self.volumeDelta = volumeDelta
        self.timestamp = timestamp
    }
}

public struct MuscleGroupStat: Codable, Sendable {
    public let muscleGroup: String
    public let volumePercentage: Double
}

public enum SummaryPeriod: String, Codable, Sendable {
    case day, week, month, year
}

public struct ExerciseInsight: Codable, Sendable {
    public let exerciseId: String
    public let estimated1RM: Double
    public let prDetected: Bool
    public let trendDirection: InsightTrend
    public let breakdownText: String
    public let timestamp: Date

    public init(exerciseId: String, estimated1RM: Double, prDetected: Bool, trendDirection: InsightTrend, breakdownText: String, timestamp: Date = Date()) {
        self.exerciseId = exerciseId
        self.estimated1RM = estimated1RM
        self.prDetected = prDetected
        self.trendDirection = trendDirection
        self.breakdownText = breakdownText
        self.timestamp = timestamp
    }
}

public enum InsightTrend: String, Codable, Sendable {
    case improving, stable, plateaued, declining
}
