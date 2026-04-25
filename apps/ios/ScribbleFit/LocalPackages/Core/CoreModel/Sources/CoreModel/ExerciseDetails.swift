import Foundation

public struct ExerciseDetails: Codable, Sendable {
    public let exerciseName: String
    public let muscleGroup: String
    public let weeklyStats: WeeklyStats
    public let trends: ExerciseTrends
    public let history: [ExerciseHistorySession]
    
    public init(exerciseName: String, muscleGroup: String, weeklyStats: WeeklyStats, trends: ExerciseTrends, history: [ExerciseHistorySession]) {
        self.exerciseName = exerciseName
        self.muscleGroup = muscleGroup
        self.weeklyStats = weeklyStats
        self.trends = trends
        self.history = history
    }
}

public struct WeeklyStats: Codable, Sendable {
    public let sessionsThisWeek: Int
    public let totalVolumeThisWeek: Float
    public let maxWeightThisWeek: Float
    
    public init(sessionsThisWeek: Int, totalVolumeThisWeek: Float, maxWeightThisWeek: Float) {
        self.sessionsThisWeek = sessionsThisWeek
        self.totalVolumeThisWeek = totalVolumeThisWeek
        self.maxWeightThisWeek = maxWeightThisWeek
    }
}

public struct ExerciseTrends: Codable, Sendable {
    public let current1RM: Float
    public let trendDirection: TrendDirection
    public let lastVolume: Float
    public let lastVolumeTrend: TrendDirection
    public let intensity: Float
    public let improvement: Float
    
    public init(
        current1RM: Float,
        trendDirection: TrendDirection,
        lastVolume: Float,
        lastVolumeTrend: TrendDirection,
        intensity: Float,
        improvement: Float
    ) {
        self.current1RM = current1RM
        self.trendDirection = trendDirection
        self.lastVolume = lastVolume
        self.lastVolumeTrend = lastVolumeTrend
        self.intensity = intensity
        self.improvement = improvement
    }
}
