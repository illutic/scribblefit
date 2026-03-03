import Foundation

public struct ParseRequest: Codable, Sendable {
    public let rawText: String
    public let prompt: String
    
    public init(rawText: String, prompt: String) {
        self.rawText = rawText
        self.prompt = prompt
    }
}
