import Foundation

public struct VolumeDataPoint: Equatable, Sendable {
    public let date: Date
    public let volume: Double

    public init(date: Date, volume: Double) {
        self.date = date
        self.volume = volume
    }
}

public struct FrequencyData: Equatable, Sendable {
    public let totalWorkouts: Int
    public let workoutsPerWeek: Double

    public init(totalWorkouts: Int, workoutsPerWeek: Double) {
        self.totalWorkouts = totalWorkouts
        self.workoutsPerWeek = workoutsPerWeek
    }
}

public struct MuscleGroupDistribution: Equatable, Sendable {
    public let muscleGroup: String
    public let percentage: Double

    public init(muscleGroup: String, percentage: Double) {
        self.muscleGroup = muscleGroup
        self.percentage = percentage
    }
}

public struct Insights: Equatable, Sendable {
    public let volumePoints: [VolumeDataPoint]
    public let frequency: FrequencyData
    public let distribution: [MuscleGroupDistribution]

    public init(volumePoints: [VolumeDataPoint], frequency: FrequencyData, distribution: [MuscleGroupDistribution]) {
        self.volumePoints = volumePoints
        self.frequency = frequency
        self.distribution = distribution
    }
}
