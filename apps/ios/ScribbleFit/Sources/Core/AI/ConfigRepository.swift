import Combine
import Foundation

public protocol ConfigRepository: Sendable {
    func getConfig() async -> SystemConfigDomain?
    func updateConfig(_ config: SystemConfigDomain) async throws
}
