import Foundation

public struct ExerciseDto: Codable, Sendable {
    public let id: String
    public let canonicalName: String
    public let muscleGroup: String
    public let aliases: [String]
    
    public init(id: String, canonicalName: String, muscleGroup: String, aliases: [String]) {
        self.id = id
        self.canonicalName = canonicalName
        self.muscleGroup = muscleGroup
        self.aliases = aliases
    }
}

public struct ExerciseResponse: Codable, Sendable {
    public let exercises: [ExerciseDto]
    
    public init(exercises: [ExerciseDto]) {
        self.exercises = exercises
    }
}
