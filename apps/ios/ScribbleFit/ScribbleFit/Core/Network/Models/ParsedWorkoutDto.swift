import Foundation

public struct ParsedWorkoutDto: Codable, Sendable {
    public let date: String
    public let location: String?
    public let exercises: [ParsedExerciseDto]
    
    public init(date: String, location: String? = nil, exercises: [ParsedExerciseDto]) {
        self.date = date
        self.location = location
        self.exercises = exercises
    }
}

public struct ParsedExerciseDto: Codable, Sendable {
    public let canonicalName: String
    public let sets: [ParsedSetDto]
    
    public init(canonicalName: String, sets: [ParsedSetDto]) {
        self.canonicalName = canonicalName
        self.sets = sets
    }
}

public struct ParsedSetDto: Codable, Sendable {
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
