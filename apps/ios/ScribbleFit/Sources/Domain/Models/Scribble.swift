import Foundation

public struct Scribble: Identifiable, Equatable, Sendable {
    public let id: UUID
    public let rawText: String
    public let parsedJson: String?
    public let status: ScribbleStatus
    public let createdAt: Date
    public let exercises: [Exercise]

    public init(
        id: UUID = UUID(),
        rawText: String,
        parsedJson: String? = nil,
        status: ScribbleStatus = .raw,
        createdAt: Date = Date(),
        exercises: [Exercise] = []
    ) {
        self.id = id
        self.rawText = rawText
        self.parsedJson = parsedJson
        self.status = status
        self.createdAt = createdAt
        self.exercises = exercises
    }
}

public enum ScribbleStatus: String, Codable, Sendable {
    case raw
    case inProgress
    case parsed
    case completed
    case failed
}
