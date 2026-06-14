import Foundation

public struct AIParsingError: Error, Sendable {
    public let rawText: String
    public let error: String

    public init(rawText: String, error: String) {
        self.rawText = rawText
        self.error = error
    }
}
