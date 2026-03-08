import Foundation

public struct WorkoutSession: Codable, Sendable {
    public let id: String
    public let startTime: Date
    public let lastActivityTime: Date
    public let exercises: [SessionExercise]
}

public struct SessionExercise: Codable, Sendable {
    public let canonicalName: String
    public let sets: [SessionSet]
}

public struct SessionSet: Codable, Sendable {
    public let weight: Double
    public let reps: Int
    public let rpe: Double?
    public let notes: String?
}
