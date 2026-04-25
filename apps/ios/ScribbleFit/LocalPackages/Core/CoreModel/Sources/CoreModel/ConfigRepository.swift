import Foundation
import Combine

@MainActor
public protocol ConfigRepository: Sendable {
    func getConfig() -> SystemConfig
    func updateConfig(_ config: SystemConfig)
    func resetConfig()
    var configPublisher: AnyPublisher<SystemConfig, Never> { get }
}
