import Foundation

public struct TelemetryData: Sendable {
    public let rawText: String
    public let promptVersion: String
    public let errorMessage: String
    public let errorCode: String?
    public let deviceModel: String?
    
    public init(rawText: String, promptVersion: String, errorMessage: String, errorCode: String? = nil, deviceModel: String? = nil) {
        self.rawText = rawText
        self.promptVersion = promptVersion
        self.errorMessage = errorMessage
        self.errorCode = errorCode
        self.deviceModel = deviceModel
    }
}

public protocol TelemetryRepository: Sendable {
    func reportError(data: TelemetryData) async throws
}
