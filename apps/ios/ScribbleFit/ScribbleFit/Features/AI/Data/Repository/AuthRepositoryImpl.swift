import Foundation

public final class AuthRepositoryImpl: AuthRepository {
    private let networkClient: ScribbleFitNetworkClient
    private let secureKeyStorage: SecureKeyStorage
    
    public init(networkClient: ScribbleFitNetworkClient = .shared, secureKeyStorage: SecureKeyStorage) {
        self.networkClient = networkClient
        self.secureKeyStorage = secureKeyStorage
    }
    
    public func login(deviceId: String) async throws {
        let request = AuthRequest(deviceId: deviceId)
        let response = try await networkClient.login(request: request)
        try await secureKeyStorage.saveAuthToken(response.token)
    }
    
    public func isLogged() async throws -> Bool {
        return try await secureKeyStorage.getAuthToken() != nil
    }
}
