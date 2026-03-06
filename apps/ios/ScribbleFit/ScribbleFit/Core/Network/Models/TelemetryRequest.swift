import Foundation

public struct TelemetryRequest: Codable, Sendable {
    public let rawText: String
    public let promptVersion: String
    public let errorMessage: String
    public let errorCode: String?
    public let deviceModel: String?
    public let timestamp: Int64
    
    public init(rawText: String, promptVersion: String, errorMessage: String, errorCode: String? = nil, deviceModel: String? = nil, timestamp: Int64 = Int64(Date().timeIntervalSince1970 * 1000)) {
        self.rawText = rawText
        self.promptVersion = promptVersion
        self.errorMessage = errorMessage
        self.errorCode = errorCode
        self.deviceModel = deviceModel
        self.timestamp = timestamp
    }
}
