import XCTest
@testable import ScribbleFit

final class AuthRepositoryImplTests: XCTestCase {
    private var repository: AuthRepositoryImpl!
    private var mockNetworkClient: ScribbleFitNetworkClient!
    private var mockSecureStorage: MockSecureKeyStorage!
    
    override func setUp() {
        super.setUp()
        // Note: Real testing would use a protocol for NetworkClient or a URLProtocol mock
        // For this scaffold, we'll assume a way to inject or mock the behavior
        mockSecureStorage = MockSecureKeyStorage()
        repository = AuthRepositoryImpl(secureKeyStorage: mockSecureStorage)
    }
    
    func testIsLoggedReturnsTrueWhenTokenExists() async throws {
        await mockSecureStorage.saveAuthToken("token")
        let isLogged = try await repository.isLogged()
        XCTAssertTrue(isLogged)
    }
    
    func testIsLoggedReturnsFalseWhenTokenIsMissing() async throws {
        let isLogged = try await repository.isLogged()
        XCTAssertFalse(isLogged)
    }
}

private final class MockSecureKeyStorage: SecureKeyStorage, @unchecked Sendable {
    private var apiKey: String?
    private var authToken: String?
    
    func saveApiKey(_ key: String) async throws { apiKey = key }
    func getApiKey() async throws -> String? { apiKey }
    func clearApiKey() async throws { apiKey = nil }
    
    func saveAuthToken(_ token: String) async throws { authToken = token }
    func getAuthToken() async throws -> String? { authToken }
    func clearAuthToken() async throws { authToken = nil }
}
