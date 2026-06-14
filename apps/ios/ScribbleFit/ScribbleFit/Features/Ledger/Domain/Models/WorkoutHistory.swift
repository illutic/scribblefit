import Foundation

public struct WorkoutHistory: Sendable, Identifiable {
    public let id: String
    public let date: Date
    public let location: String?
    public let totalVolume: Double
    public let exercises: [ExerciseHistory]

    public init(id: String, date: Date, location: String?, totalVolume: Double, exercises: [ExerciseHistory]) {
        self.id = id
        self.date = date
        self.location = location
        self.totalVolume = totalVolume
        self.exercises = exercises
    }
}

public struct ExerciseHistory: Sendable, Identifiable {
    public var id: String { canonicalName }
    public let canonicalName: String
    public let sets: [SetHistory]

    public init(canonicalName: String, sets: [SetHistory]) {
        self.canonicalName = canonicalName
        self.sets = sets
    }
}

public struct SetHistory: Sendable, Identifiable {
    public var id: UUID = UUID()
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
