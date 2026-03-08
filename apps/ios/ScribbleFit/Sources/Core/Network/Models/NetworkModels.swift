import Foundation

public struct MetadataResponse: Codable, Sendable {
    public let promptVersion: String
    public let promptText: String
    public let exerciseVersion: String

    enum CodingKeys: String, CodingKey {
        case promptVersion = "prompt_version"
        case promptText = "prompt_text"
        case exerciseVersion = "exercise_version"
    }
}

public struct ExerciseDto: Codable, Sendable {
    public let id: String
    public let canonicalName: String
    public let muscleGroup: String
    public let aliases: [String]

    enum CodingKeys: String, CodingKey {
        case id
        case canonicalName = "canonical_name"
        case muscleGroup = "muscle_group"
        case aliases
    }
}

public struct TelemetryRequest: Codable, Sendable {
    public let event: String
    public let properties: [String: String]
}
