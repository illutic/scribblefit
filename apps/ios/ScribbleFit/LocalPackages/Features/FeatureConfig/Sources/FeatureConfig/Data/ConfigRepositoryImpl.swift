import Foundation
import Combine
import CoreModel
import CoreFirebase

@MainActor
public final class ConfigRepositoryImpl: ConfigRepository, @unchecked Sendable {
    private let userDefaults: UserDefaults
    private let configKey = "com.scribblefit.system_config"
    private let subject: CurrentValueSubject<SystemConfig, Never>
    
    public init(userDefaults: UserDefaults = .standard) {
        self.userDefaults = userDefaults
        
        // Load initial config or use default
        let config: SystemConfig
        if let data = userDefaults.data(forKey: configKey),
           let decoded = try? JSONDecoder().decode(SystemConfigData.self, from: data) {
            config = decoded.toDomain()
        } else {
            config = SystemConfig()
        }
        
        self.subject = CurrentValueSubject(config)
        
        // Initial fetch from remote
        Task {
            await fetchRemoteConfig()
        }
    }
    
    public func getConfig() -> SystemConfig {
        subject.value
    }
    
    public func syncMetadata() async throws {
        let remote = await RemoteConfigService.shared.fetchConfig()
        var current = subject.value
        current.remoteConfig = remote
        subject.send(current)
    }
    
    public func updateConfig(_ config: SystemConfig) {
        if let encoded = try? JSONEncoder().encode(SystemConfigData(from: config)) {
            userDefaults.set(encoded, forKey: configKey)
            subject.send(config)
        }
    }
    
    public func resetConfig() {
        let defaultConfig = SystemConfig()
        updateConfig(defaultConfig)
    }
    
    public var configPublisher: AnyPublisher<SystemConfig, Never> {
        subject.eraseToAnyPublisher()
    }
}

// Codable representation for storage (LOCAL CONFIG ONLY)
private struct SystemConfigData: Codable {
    let preferredLlmProvider: String
    let updatedAt: Date
    let weightUnit: String
    let themePreference: String
    let isDynamicTheme: Bool
    
    init(from domain: SystemConfig) {
        self.preferredLlmProvider = domain.preferredLlmProvider.rawValue
        self.updatedAt = domain.updatedAt
        self.weightUnit = domain.weightUnit.rawValue
        self.themePreference = domain.themePreference.rawValue
        self.isDynamicTheme = domain.isDynamicTheme
    }
    
    func toDomain() -> SystemConfig {
        SystemConfig(
            remoteConfig: CoreModel.RemoteConfig(), // Initial default, will be updated by fetch
            preferredLlmProvider: LLMProvider(rawValue: preferredLlmProvider) ?? .local,
            updatedAt: updatedAt,
            weightUnit: WeightUnit(rawValue: weightUnit) ?? .kgs,
            themePreference: ThemePreference(rawValue: themePreference) ?? .system,
            isDynamicTheme: isDynamicTheme
        )
    }
}
