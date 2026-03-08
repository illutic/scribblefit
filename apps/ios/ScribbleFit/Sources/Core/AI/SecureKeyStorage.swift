import Foundation

public protocol SecureKeyStorage: Sendable {
    func saveApiKey(_ key: String) async throws
    func getApiKey() async -> String?
    func clearApiKey() async throws
}
