import Foundation
import Combine
import CoreModel

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
    }
    
    public func getConfig() -> SystemConfig {
        subject.value
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

// Codable representation for storage
private struct SystemConfigData: Codable {
    let summaryPrompt: String
    let suggestionPrompt: String
    let insightPrompt: String
    let parsePrompt: String
    let preferredLlmProvider: String
    let updatedAt: Date
    let preferredModel: String?
    let weightUnit: String
    let themePreference: String
    let isDynamicTheme: Bool
    
    init(from domain: SystemConfig) {
        self.summaryPrompt = domain.summaryPrompt
        self.suggestionPrompt = domain.suggestionPrompt
        self.insightPrompt = domain.insightPrompt
        self.parsePrompt = domain.parsePrompt
        self.preferredLlmProvider = domain.preferredLlmProvider.rawValue
        self.updatedAt = domain.updatedAt
        self.preferredModel = domain.preferredModel
        self.weightUnit = domain.weightUnit.rawValue
        self.themePreference = domain.themePreference.rawValue
        self.isDynamicTheme = domain.isDynamicTheme
    }
    
    func toDomain() -> SystemConfig {
        SystemConfig(
            summaryPrompt: summaryPrompt,
            suggestionPrompt: suggestionPrompt,
            insightPrompt: insightPrompt,
            parsePrompt: parsePrompt,
            preferredLlmProvider: LLMProvider(rawValue: preferredLlmProvider) ?? .local,
            updatedAt: updatedAt,
            preferredModel: preferredModel,
            weightUnit: WeightUnit(rawValue: weightUnit) ?? .kgs,
            themePreference: ThemePreference(rawValue: themePreference) ?? .system,
            isDynamicTheme: isDynamicTheme
        )
    }
}
