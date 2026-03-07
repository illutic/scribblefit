import Foundation

public struct AnalysisSummary: Sendable {
    public let period: SummaryPeriod
    public let summaryText: String
    public let highlights: [String]
    public let muscleDistribution: [MuscleGroupStat]
    public let focusArea: String
    public let volumeDelta: Double
    public let timestamp: Date
    
    public init(
        period: SummaryPeriod,
        summaryText: String,
        highlights: [String],
        muscleDistribution: [MuscleGroupStat],
        focusArea: String,
        volumeDelta: Double,
        timestamp: Date
    ) {
        self.period = period
        self.summaryText = summaryText
        self.highlights = highlights
        self.muscleDistribution = muscleDistribution
        self.focusArea = focusArea
        self.volumeDelta = volumeDelta
        self.timestamp = timestamp
    }
}

public struct MuscleGroupStat: Sendable {
    public let muscleGroup: String
    public let volumePercentage: Double
    
    public init(muscleGroup: String, volumePercentage: Double) {
        self.muscleGroup = muscleGroup
        self.volumePercentage = volumePercentage
    }
}

public enum SummaryPeriod: String, Codable, Sendable {
    case day, week, month, year
}
