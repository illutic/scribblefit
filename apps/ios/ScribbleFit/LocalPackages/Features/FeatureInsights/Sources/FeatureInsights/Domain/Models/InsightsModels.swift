import Foundation
import CoreModel

public struct VolumeDataPoint: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let date: Date
    public let volume: Float

    public init(id: UUID = UUID(), date: Date, volume: Float) {
        self.id = id
        self.date = date
        self.volume = volume
    }
}

public struct FrequencyData: Equatable, Sendable {
    public let totalWorkouts: Int
    public let workoutsPerWeek: Float
    public let totalExercises: Int

    public init(totalWorkouts: Int, workoutsPerWeek: Float, totalExercises: Int = 0) {
        self.totalWorkouts = totalWorkouts
        self.workoutsPerWeek = workoutsPerWeek
        self.totalExercises = totalExercises
    }
}

public struct MuscleGroupDistribution: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let muscleGroup: String
    public let percentage: Float

    public init(id: UUID = UUID(), muscleGroup: String, percentage: Float) {
        self.id = id
        self.muscleGroup = muscleGroup
        self.percentage = percentage
    }
}

public struct AIOverview: Equatable, Sendable {
    public let insights: [AIInsight]

    public init(insights: [AIInsight]) {
        self.insights = insights
    }
}
