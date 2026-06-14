import Foundation

/// A simple Codable DTO for AI workout data, used by Cloud providers.
struct AIWorkoutDTO: Codable {
    let date: String
    let location: String?
    let exercises: [AIExerciseDTO]

    func toDomain() -> ParsedWorkout {
        return ParsedWorkout(
            date: self.date,
            location: self.location,
            exercises: self.exercises.map { $0.toDomain() }
        )
    }
}

struct AIExerciseDTO: Codable {
    let canonicalName: String
    let sets: [AISetDTO]

    enum CodingKeys: String, CodingKey {
        case canonicalName = "canonical_name"
        case sets
    }

    func toDomain() -> ParsedExercise {
        return ParsedExercise(
            canonicalName: self.canonicalName,
            sets: self.sets.map { $0.toDomain() }
        )
    }
}

struct AISetDTO: Codable {
    let weight: Double
    let reps: Int
    let rpe: Double?
    let notes: String?

    func toDomain() -> ParsedSet {
        return ParsedSet(
            weight: self.weight,
            reps: self.reps,
            rpe: self.rpe,
            notes: self.notes
        )
    }
}
