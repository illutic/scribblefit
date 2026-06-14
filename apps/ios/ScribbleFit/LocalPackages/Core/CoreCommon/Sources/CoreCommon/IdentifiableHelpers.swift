import Foundation

public struct IdentifiableString: Identifiable, Sendable {
    public let id: String
    public let value: String

    public init(value: String) {
        self.id = value
        self.value = value
    }
}

public struct IdentifiableUUID: Identifiable, Sendable {
    public let id: UUID
    public let value: UUID

    public init(value: UUID) {
        self.id = value
        self.value = value
    }
}

public enum Screen: Hashable, Sendable {
    case settings
    case profile
}
