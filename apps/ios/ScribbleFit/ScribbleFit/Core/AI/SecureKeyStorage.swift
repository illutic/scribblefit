import Foundation

public protocol SecureKeyStorage: Sendable {
    func saveApiKey(_ key: String) async throws
    func getApiKey() async throws -> String?
    func clearApiKey() async throws
    
    func saveAuthToken(_ token: String) async throws
    func getAuthToken() async throws -> String?
    func clearAuthToken() async throws
}
