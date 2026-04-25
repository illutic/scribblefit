import Foundation

public protocol SettingsRepository: Sendable {
    func exportUserData() async throws -> URL
    func clearAllData() async throws
}
