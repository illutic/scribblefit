import Foundation

public struct ConfigResponse: Codable, Sendable {
    public let version: String
    public let prompt: String
    
    public init(version: String, prompt: String) {
        self.version = version
        self.prompt = prompt
    }
}
