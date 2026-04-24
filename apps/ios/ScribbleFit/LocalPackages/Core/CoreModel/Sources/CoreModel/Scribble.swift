import Foundation

public struct Scribble: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public var rawText: String
    public var status: ScribbleStatus
    public let createdAt: Date
    public var parsedJson: String?
    public var exercises: [Exercise]

    public init(
        id: UUID = UUID(),
        rawText: String,
        status: ScribbleStatus,
        createdAt: Date = Date(),
        parsedJson: String? = nil,
        exercises: [Exercise] = []
    ) {
        self.id = id
        self.rawText = rawText
        self.status = status
        self.createdAt = createdAt
        self.parsedJson = parsedJson
        self.exercises = exercises
    }

    public func copy(
        rawText: String? = nil,
        status: ScribbleStatus? = nil,
        parsedJson: String?? = nil,
        exercises: [Exercise]? = nil
    ) -> Scribble {
        var newScribble = self
        if let rawText = rawText { newScribble.rawText = rawText }
        if let status = status { newScribble.status = status }
        if let parsedJson = parsedJson { newScribble.parsedJson = parsedJson }
        if let exercises = exercises { newScribble.exercises = exercises }
        return newScribble
    }
}

public enum ScribbleStatus: String, Codable, Sendable {
    case pending = "PENDING"
    case parsing = "PARSING"
    case success = "SUCCESS"
    case failed = "FAILED"
    case completed = "COMPLETED"
}
