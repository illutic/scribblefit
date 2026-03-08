import Foundation

public enum FeedItem: Sendable, Identifiable {
    case prompt(PromptItem)
    case scribble(ScribbleItem)
    case confirmation(ConfirmationItem)
    case insight(InsightItem)

    public var id: String {
        switch self {
        case .prompt(let item): return item.id
        case .scribble(let item): return item.id
        case .confirmation(let item): return item.id
        case .insight(let item): return item.id
        }
    }

    public var timestamp: Date {
        switch self {
        case .prompt(let item): return item.timestamp
        case .scribble(let item): return item.timestamp
        case .confirmation(let item): return item.timestamp
        case .insight(let item): return item.timestamp
        }
    }
}

public struct PromptItem: Identifiable, Codable, Sendable {
    public let id: String
    public let timestamp: Date
    public let text: String
    public let emoji: String
    public let type: SuggestionType
}

public struct ScribbleItem: Identifiable, Sendable {
    public let id: String
    public let timestamp: Date
    public let rawText: String
    public let status: ScribbleStatus
}

public struct ConfirmationItem: Identifiable, Codable, Sendable {
    public let id: String
    public let timestamp: Date
    public let workout: ParsedWorkout
    public let scribbleId: String
}

public struct InsightItem: Identifiable, Codable, Sendable {
    public let id: String
    public let timestamp: Date
    public let text: String
    public let emoji: String
}

public enum ScribbleStatus: Sendable {
    case pending, processing, failed, completed
}
