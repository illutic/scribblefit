import Foundation

public protocol AuthRepository: Sendable {
    func login(deviceId: String) async throws
    func isLogged() async throws -> Bool
}
