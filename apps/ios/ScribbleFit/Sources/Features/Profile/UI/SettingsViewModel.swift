import Foundation
import Combine

public struct SettingsUiState: Sendable {
    public var config: SystemConfigDomain = SystemConfigDomain()
    public var availableModels: [String] = []
    public var isLoadingModels: Bool = false
    public var apiKey: String = ""
    public var showApiKeyField: Bool = false
}

@MainActor
public final class SettingsViewModel: ObservableObject {
    @Published public var uiState = SettingsUiState()

    private let configRepository: any ConfigRepository
    private let modelRepository: any ModelRepository
    private let secureKeyStorage: any SecureKeyStorage

    public init(
        configRepository: any ConfigRepository,
        modelRepository: any ModelRepository,
        secureKeyStorage: any SecureKeyStorage
    ) {
        self.configRepository = configRepository
        self.modelRepository = modelRepository
        self.secureKeyStorage = secureKeyStorage
        Task { await loadConfig() }
    }

    public func loadConfig() async {
        uiState.config = (await configRepository.getConfig()) ?? SystemConfigDomain()
        uiState.apiKey = await secureKeyStorage.getApiKey() ?? ""
        uiState.showApiKeyField = uiState.config.preferredLlmProvider != .proxy && uiState.config.preferredLlmProvider != .local
        if uiState.showApiKeyField && !uiState.apiKey.isEmpty {
            await fetchModels()
        }
    }

    public func onProviderChanged(_ provider: LLMProvider) async {
        let updated = SystemConfigDomain(
            summaryPrompt: uiState.config.summaryPrompt,
            suggestionPrompt: uiState.config.suggestionPrompt,
            insightPrompt: uiState.config.insightPrompt,
            parsePrompt: uiState.config.parsePrompt,
            preferredLlmProvider: provider,
            preferredModel: nil,
            weightUnit: uiState.config.weightUnit,
            themePreference: uiState.config.themePreference,
            updatedAt: Date()
        )
        uiState.config = updated
        uiState.availableModels = []
        uiState.showApiKeyField = provider != .proxy && provider != .local
        try? await configRepository.updateConfig(updated)
        if uiState.showApiKeyField && !uiState.apiKey.isEmpty {
            await fetchModels()
        }
    }

    public func onModelSelected(_ model: String) {
        let updated = SystemConfigDomain(
            summaryPrompt: uiState.config.summaryPrompt,
            suggestionPrompt: uiState.config.suggestionPrompt,
            insightPrompt: uiState.config.insightPrompt,
            parsePrompt: uiState.config.parsePrompt,
            preferredLlmProvider: uiState.config.preferredLlmProvider,
            preferredModel: model,
            weightUnit: uiState.config.weightUnit,
            themePreference: uiState.config.themePreference,
            updatedAt: Date()
        )
        uiState.config = updated
        Task { try? await configRepository.updateConfig(updated) }
    }

    public func onApiKeySaved(_ key: String) async {
        try? await secureKeyStorage.saveApiKey(key)
        uiState.apiKey = key
        await fetchModels()
    }

    public func onWeightUnitChanged(_ unit: Weight) async {
        let updated = SystemConfigDomain(
            summaryPrompt: uiState.config.summaryPrompt,
            suggestionPrompt: uiState.config.suggestionPrompt,
            insightPrompt: uiState.config.insightPrompt,
            parsePrompt: uiState.config.parsePrompt,
            preferredLlmProvider: uiState.config.preferredLlmProvider,
            preferredModel: uiState.config.preferredModel,
            weightUnit: unit,
            themePreference: uiState.config.themePreference,
            updatedAt: Date()
        )
        uiState.config = updated
        try? await configRepository.updateConfig(updated)
    }

    public func onThemeChanged(_ theme: ThemePreference) async {
        let updated = SystemConfigDomain(
            summaryPrompt: uiState.config.summaryPrompt,
            suggestionPrompt: uiState.config.suggestionPrompt,
            insightPrompt: uiState.config.insightPrompt,
            parsePrompt: uiState.config.parsePrompt,
            preferredLlmProvider: uiState.config.preferredLlmProvider,
            preferredModel: uiState.config.preferredModel,
            weightUnit: uiState.config.weightUnit,
            themePreference: theme,
            updatedAt: Date()
        )
        uiState.config = updated
        try? await configRepository.updateConfig(updated)
    }

    public func onClearDataTapped() async {
        await configRepository.resetConfig()
        try? await secureKeyStorage.clearApiKey()
        uiState = SettingsUiState()
    }

    public func fetchModels() async {
        let key = uiState.apiKey
        guard !key.isEmpty else { return }
        uiState.isLoadingModels = true
        uiState.availableModels = (try? await modelRepository.fetchModels(for: uiState.config.preferredLlmProvider, apiKey: key)) ?? []
        uiState.isLoadingModels = false
    }
}
