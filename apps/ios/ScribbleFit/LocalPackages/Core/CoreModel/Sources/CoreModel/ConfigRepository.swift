import Foundation
import Combine

@MainActor
public protocol ConfigRepository: Sendable {
    func getConfig() -> SystemConfig
    func updateConfig(_ config: SystemConfig)
    func resetConfig()
    func fetchRemoteConfig() async throws
    var configPublisher: AnyPublisher<SystemConfig, Never> { get }
}
