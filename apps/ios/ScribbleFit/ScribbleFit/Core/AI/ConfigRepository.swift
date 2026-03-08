import Foundation

public protocol ConfigRepository: Sendable {
    func getConfig() async -> SystemConfig?
    func updateConfig(_ config: SystemConfig) async
    func syncMetadata() async throws
}
