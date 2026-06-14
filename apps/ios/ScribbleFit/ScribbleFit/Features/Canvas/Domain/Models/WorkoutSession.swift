import Foundation

/**
 * Represents a transient workout session before it's committed to the ledger.
 */
public struct WorkoutSession: Codable, Sendable, Identifiable {
    public let id: String
    public let startTime: Date
    public let lastActivityTime: Date
    public let exercises: [SessionExercise]

    public init(id: String, startTime: Date, lastActivityTime: Date, exercises: [SessionExercise]) {
        self.id = id
        self.startTime = startTime
        self.lastActivityTime = lastActivityTime
        self.exercises = exercises
    }
}

public struct SessionExercise: Codable, Sendable {
    public let canonicalName: String
    public let sets: [SessionSet]

    public init(canonicalName: String, sets: [SessionSet]) {
        self.canonicalName = canonicalName
        self.sets = sets
    }
}

public struct SessionSet: Codable, Sendable {
    public let weight: Double
    public let reps: Int
    public let rpe: Double?
    public let notes: String?

    public init(weight: Double, reps: Int, rpe: Double?, notes: String?) {
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
    }
}
