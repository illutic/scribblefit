import Foundation

public struct ParsedWorkout: Sendable {
    public let date: String
    public let location: String?
    public let exercises: [ParsedExercise]
    
    public init(date: String, location: String? = nil, exercises: [ParsedExercise]) {
        self.date = date
        self.location = location
        self.exercises = exercises
    }
}

public struct ParsedExercise: Sendable {
    public let canonicalName: String
    public let sets: [ParsedSet]
    
    public init(canonicalName: String, sets: [ParsedSet]) {
        self.canonicalName = canonicalName
        self.sets = sets
    }
}

public struct ParsedSet: Sendable {
    public let weight: Double
    public let reps: Int
    public let rpe: Double?
    public let notes: String?
    
    public init(weight: Double, reps: Int, rpe: Double? = nil, notes: String? = nil) {
        self.weight = weight
        self.reps = reps
        self.rpe = rpe
        self.notes = notes
    }
}
