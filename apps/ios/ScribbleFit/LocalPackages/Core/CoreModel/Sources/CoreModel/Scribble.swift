import Foundation

public struct Scribble: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public var rawText: String
    public var status: ScribbleStatus
    public let createdAt: Date
    public var parsedJson: String?
    public var workoutId: UUID?
    public var exercises: [Exercise]

    public init(
        id: UUID = UUID(),
        rawText: String,
        status: ScribbleStatus,
        createdAt: Date = Date(),
        parsedJson: String? = nil,
        workoutId: UUID? = nil,
        exercises: [Exercise] = []
    ) {
        self.id = id
        self.rawText = rawText
        self.status = status
        self.createdAt = createdAt
        self.parsedJson = parsedJson
        self.workoutId = workoutId
        self.exercises = exercises
    }
}

public enum ScribbleStatus: String, Codable, Sendable {
    case pending = "PENDING"
    case parsing = "PARSING"
    case success = "SUCCESS"
    case failed = "FAILED"
    case completed = "COMPLETED"
}
