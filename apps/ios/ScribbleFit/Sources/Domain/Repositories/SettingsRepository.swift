import Foundation

public protocol SettingsRepository: Sendable {
    func getApiKey() async throws -> String?
    func saveApiKey(_ apiKey: String) async throws
    func clearApiKey() async throws
    func exportUserData() async throws -> URL
    func clearAllData() async throws
}
