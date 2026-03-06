import Foundation
import Security

/**
 * iOS implementation of SecureKeyStorage using the System Keychain.
 */
public final class KeychainSecureKeyStorage: SecureKeyStorage {
    private let service = "com.scribblefit.ai.service"
    private let apiKeyAccount = "api_key"
    private let authTokenAccount = "auth_token"
    
    public init() {}
    
    // MARK: - API Key
    
    public func saveApiKey(_ key: String) async throws {
        try await save(key, for: apiKeyAccount)
    }
    
    public func getApiKey() async throws -> String? {
        try await get(for: apiKeyAccount)
    }
    
    public func clearApiKey() async throws {
        try await delete(for: apiKeyAccount)
    }
    
    // MARK: - Auth Token
    
    public func saveAuthToken(_ token: String) async throws {
        try await save(token, for: authTokenAccount)
    }
    
    public func getAuthToken() async throws -> String? {
        try await get(for: authTokenAccount)
    }
    
    public func clearAuthToken() async throws {
        try await delete(for: authTokenAccount)
    }
    
    // MARK: - Private Helpers
    
    private func save(_ value: String, for account: String) async throws {
        let data = Data(value.utf8)
        
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
            kSecValueData as String: data
        ]
        
        // Try to delete any existing item first
        SecItemDelete(query as CFDictionary)
        
        let status = SecItemAdd(query as CFDictionary, nil)
        guard status == errSecSuccess else {
            throw KeychainError.unhandledError(status)
        }
    }
    
    private func get(for account: String) async throws -> String? {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account,
            kSecReturnData as String: true,
            kSecMatchLimit as String: kSecMatchLimitOne
        ]
        
        var dataTypeRef: AnyObject?
        let status = SecItemCopyMatching(query as CFDictionary, &dataTypeRef)
        
        if status == errSecItemNotFound {
            return nil
        }
        
        guard status == errSecSuccess, let data = dataTypeRef as? Data else {
            throw KeychainError.unhandledError(status)
        }
        
        return String(data: data, encoding: .utf8)
    }
    
    private func delete(for account: String) async throws {
        let query: [String: Any] = [
            kSecClass as String: kSecClassGenericPassword,
            kSecAttrService as String: service,
            kSecAttrAccount as String: account
        ]
        
        let status = SecItemDelete(query as CFDictionary)
        guard status == errSecSuccess || status == errSecItemNotFound else {
            throw KeychainError.unhandledError(status)
        }
    }
}

public enum KeychainError: Error {
    case unhandledError(OSStatus)
}
