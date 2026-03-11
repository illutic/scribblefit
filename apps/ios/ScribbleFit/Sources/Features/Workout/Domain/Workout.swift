import Foundation

public struct Workout: Sendable {
    public let date: String
    public let exercises: [Exercise]

    public init(date: String, exercises: [Exercise]) {
        self.date = date
        self.exercises = exercises
    }
}

public struct Exercise: Sendable {
    public let canonicalName: String
    public let muscleGroup: String
    public let sets: [ExerciseSet]

    public init(canonicalName: String, muscleGroup: String, sets: [ExerciseSet]) {
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.sets = sets
    }
}

public struct ExerciseSet: Sendable {
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
