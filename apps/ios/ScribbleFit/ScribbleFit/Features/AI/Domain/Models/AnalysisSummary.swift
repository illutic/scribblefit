import Foundation

public struct AnalysisSummary: Codable, Sendable {
    public let period: SummaryPeriod
    public let summaryText: String
    public let highlights: [String]
    public let focusMuscleGroups: [String]
    public let volumeDelta: Double
    public let timestamp: Date
    
    public init(
        period: SummaryPeriod,
        summaryText: String,
        highlights: [String],
        focusMuscleGroups: [String],
        volumeDelta: Double,
        timestamp: Date
    ) {
        self.period = period
        self.summaryText = summaryText
        self.highlights = highlights
        self.focusMuscleGroups = focusMuscleGroups
        self.volumeDelta = volumeDelta
        self.timestamp = timestamp
    }
}

public enum SummaryPeriod: String, Codable, Sendable {
    case week
    case month
    case year
}
