import Foundation
import Combine

public struct SettingsUiState: Sendable {
    public var settings: AppSettings = AppSettings()
    public var availableModels: [String] = []
    public var isLoadingModels: Bool = false
    public var isSaving: Bool = false
    public var apiKey: String = ""
    public var showApiKeyField: Bool = false
}

@MainActor
public final class SettingsViewModel: ObservableObject {
    @Published public var uiState = SettingsUiState()

    private let settingsRepository: any SettingsRepository
    private let modelRepository: any ModelRepository
    private let secureKeyStorage: any SecureKeyStorage

    public init(
        settingsRepository: any SettingsRepository,
        modelRepository: any ModelRepository,
        secureKeyStorage: any SecureKeyStorage
    ) {
        self.settingsRepository = settingsRepository
        self.modelRepository = modelRepository
        self.secureKeyStorage = secureKeyStorage
        Task { await loadSettings() }
    }

    public func loadSettings() async {
        uiState.settings = (try? await settingsRepository.getSettings()) ?? AppSettings()
        uiState.apiKey = await secureKeyStorage.getApiKey() ?? ""
        uiState.showApiKeyField = uiState.settings.aiProvider != .proxy
    }

    public func onProviderChanged(_ provider: LLMProvider) async {
        uiState.settings.aiProvider = provider
        uiState.settings.selectedModel = nil
        uiState.availableModels = []
        uiState.showApiKeyField = provider != .proxy && provider != .local
        try? await settingsRepository.updateSettings(uiState.settings)
        if provider != .proxy && provider != .local && !uiState.apiKey.isEmpty {
            await fetchModels()
        }
    }

    public func onModelSelected(_ model: String) {
        uiState.settings.selectedModel = model
        Task { try? await settingsRepository.updateSettings(uiState.settings) }
    }

    public func onApiKeySaved(_ key: String) async {
        try? await secureKeyStorage.saveApiKey(key)
        uiState.apiKey = key
        await fetchModels()
    }

    public func onWeightUnitChanged(_ unit: WeightUnit) async {
        uiState.settings.weightUnit = unit
        try? await settingsRepository.updateSettings(uiState.settings)
    }

    public func onThemeChanged(_ theme: ThemePreference) async {
        uiState.settings.themePreference = theme
        try? await settingsRepository.updateSettings(uiState.settings)
    }

    public func onClearDataTapped() async {
        try? await settingsRepository.clearAllData()
        try? await secureKeyStorage.clearApiKey()
        uiState = SettingsUiState()
    }

    public func fetchModels() async {
        let key = uiState.apiKey
        guard !key.isEmpty else { return }
        uiState.isLoadingModels = true
        uiState.availableModels = (try? await modelRepository.fetchModels(for: uiState.settings.aiProvider, apiKey: key)) ?? []
        uiState.isLoadingModels = false
    }
}
