import Foundation

public struct AnalysisSuggestion: Codable, Sendable {
    public let text: String
    public let emoji: String
    public let type: SuggestionType
    public let timestamp: Date

    public init(text: String, emoji: String, type: SuggestionType, timestamp: Date) {
        self.text = text
        self.emoji = emoji
        self.type = type
        self.timestamp = timestamp
    }

    public var fullText: String { "\(text) \(emoji)" }
}

public enum SuggestionType: String, Codable, Sendable {
    case recovery
    case pattern
    case milestone
    case rest
}
