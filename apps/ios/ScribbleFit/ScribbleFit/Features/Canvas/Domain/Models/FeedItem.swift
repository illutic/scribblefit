import Foundation

/**
 * Represents an item in the Intelligent Canvas feed.
 */
public enum FeedItem: Sendable, Identifiable {
    case prompt(PromptItem)
    case scribble(ScribbleItem)
    case confirmation(ConfirmationItem)
    case insight(InsightItem)
    
    public var id: String {
        switch self {
        case .prompt(let item): item.id
        case .scribble(let item): item.id
        case .confirmation(let item): item.id
        case .insight(let item): item.id
        }
    }
    
    public var timestamp: Date {
        switch self {
        case .prompt(let item): item.timestamp
        case .scribble(let item): item.timestamp
        case .confirmation(let item): item.timestamp
        case .insight(let item): item.timestamp
        }
    }
}

public struct PromptItem: Sendable, Identifiable {
    public let id: String
    public let timestamp: Date
    public let text: String
    public let emoji: String
    public let type: SuggestionType
}

public struct ScribbleItem: Sendable, Identifiable {
    public let id: String
    public let timestamp: Date
    public let rawText: String
    public let status: ScribbleStatus
}

public struct ConfirmationItem: Sendable, Identifiable {
    public let id: String
    public let timestamp: Date
    public let workout: ParsedWorkout
    public let scribbleId: String
}

public struct InsightItem: Sendable, Identifiable {
    public let id: String
    public let timestamp: Date
    public let text: String
    public let emoji: String
}

public enum ScribbleStatus: String, Codable, Sendable {
    case pending, processing, failed, completed
}
