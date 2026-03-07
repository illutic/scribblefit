import Foundation

public protocol UserRepository: Sendable {
    func getUserStats() async throws -> UserStats
}

public protocol SettingsRepository: Sendable {
    func getSettings() async throws -> AppSettings
    func updateSettings(_ settings: AppSettings) async throws
    func clearAllData() async throws
}
