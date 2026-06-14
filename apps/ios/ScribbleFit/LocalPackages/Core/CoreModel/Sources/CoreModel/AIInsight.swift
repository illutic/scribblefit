import Foundation

public enum InsightType: String, Codable, Sendable {
    case summary
    case trend
    case advice
}

public struct AIInsight: Identifiable, Equatable, Sendable, Codable {
    public let id: UUID
    public let insightType: InsightType
    public let text: String

    public init(id: UUID = UUID(), insightType: InsightType, text: String) {
        self.id = id
        self.insightType = insightType
        self.text = text
    }
}
