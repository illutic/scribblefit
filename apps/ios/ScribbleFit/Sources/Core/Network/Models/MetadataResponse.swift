import Foundation

public struct MetadataResponse: Codable, Sendable {
    public let status: String
    public let appVersion: String
    public let promptVersion: String
    public let exerciseVersion: String
    
    public init(status: String, appVersion: String, promptVersion: String, exerciseVersion: String) {
        self.status = status
        self.appVersion = appVersion
        self.promptVersion = promptVersion
        self.exerciseVersion = exerciseVersion
    }
}
