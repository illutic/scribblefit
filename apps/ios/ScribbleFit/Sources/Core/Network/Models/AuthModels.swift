import Foundation

public struct AuthRequest: Codable, Sendable {
    public let deviceId: String
    public let subscriptionId: String?
    
    public init(deviceId: String, subscriptionId: String? = nil) {
        self.deviceId = deviceId
        self.subscriptionId = subscriptionId
    }
}

public struct AuthResponse: Codable, Sendable {
    public let token: String
    public let expiresAt: Int64
    
    public init(token: String, expiresAt: Int64) {
        self.token = token
        self.expiresAt = expiresAt
    }
}
