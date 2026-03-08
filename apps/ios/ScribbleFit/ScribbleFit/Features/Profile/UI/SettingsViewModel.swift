import Foundation
import SwiftUI
import Combine

@MainActor
public final class SettingsViewModel: ObservableObject {
    private let settingsRepository: SettingsRepository
    private let modelRepository: ModelRepository
    private let secureKeyStorage: SecureKeyStorage
    private let navManager: NavigationManager

    @Published public var uiState = SettingsUiState()

    public init(
        settingsRepository: SettingsRepository,
        modelRepository: ModelRepository,
        secureKeyStorage: SecureKeyStorage,
        navManager: NavigationManager
    ) {
        self.settingsRepository = settingsRepository
        self.modelRepository = modelRepository
        self.secureKeyStorage = secureKeyStorage
        self.navManager = navManager

        loadSettings()
    }

    private func loadSettings() {
        Task {
            do {
                let settings = try await settingsRepository.getSettings()
                let apiKey = try await secureKeyStorage.getApiKey() ?? ""
                self.uiState = SettingsUiState(settings: settings, apiKey: apiKey, isLoading: false)
                if (settings.aiProvider == .openai || settings.aiProvider == .gemini) && !apiKey.isEmpty {
                    await fetchModels()
                }
            } catch {
                print("Failed to load settings: \(error)")
                self.uiState.isLoading = false
            }
        }
    }

    public func updateParsingMode(_ mode: ParsingMode) {
        updateSettings { current in
            AppSettings(parsingMode: mode, aiProvider: current.aiProvider, weightUnit: current.weightUnit, themePreference: current.themePreference, selectedModel: current.selectedModel)
        }
    }

    public func updateApiKey(_ key: String) {
        uiState.apiKey = key
        Task {
            try? await secureKeyStorage.saveApiKey(key)
            let provider = uiState.settings.aiProvider
            if (provider == .openai || provider == .gemini) && !key.isEmpty {
                await fetchModels()
            } else {
                uiState.availableModels = []
            }
        }
    }

    public func updateProvider(_ provider: LLMProvider) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: provider, weightUnit: current.weightUnit, themePreference: current.themePreference, selectedModel: nil)
        }
        uiState.availableModels = []
        if (provider == .openai || provider == .gemini) && !uiState.apiKey.isEmpty {
            Task { await fetchModels() }
        }
    }

    public func updateModel(_ model: String) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: current.aiProvider, weightUnit: current.weightUnit, themePreference: current.themePreference, selectedModel: model)
        }
    }

    public func updateWeightUnit(_ unit: WeightUnit) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: current.aiProvider, weightUnit: unit, themePreference: current.themePreference, selectedModel: current.selectedModel)
        }
    }

    public func updateTheme(_ theme: ThemePreference) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: current.aiProvider, weightUnit: current.weightUnit, themePreference: theme, selectedModel: current.selectedModel)
        }
    }

    public func onBackClick() {
        navManager.pop(in: .profile)
    }

    public func onClearDataClick() {
        Task {
            try? await settingsRepository.clearAllData()
        }
    }

    public func fetchModels() async {
        let provider = uiState.settings.aiProvider
        let apiKey = uiState.apiKey
        guard !apiKey.isEmpty, provider == .openai || provider == .gemini else { return }

        uiState.isLoadingModels = true
        uiState.modelLoadError = nil

        do {
            let models = try await modelRepository.fetchModels(for: provider, apiKey: apiKey)
            uiState.availableModels = models
            if uiState.settings.selectedModel == nil, let first = models.first {
                updateModel(first)
            }
        } catch {
            uiState.modelLoadError = "Failed to load models"
        }
        uiState.isLoadingModels = false
    }

    private func updateSettings(_ transform: @escaping (AppSettings) -> AppSettings) {
        Task {
            let current = uiState.settings
            let new = transform(current)
            self.uiState.settings = new
            try? await settingsRepository.updateSettings(new)
        }
    }
}

public struct SettingsUiState {
    public var settings: AppSettings = AppSettings(parsingMode: .managed, aiProvider: .proxy, weightUnit: .lbs, themePreference: .system)
    public var apiKey: String = ""
    public var isLoading: Bool = true
    public var availableModels: [String] = []
    public var isLoadingModels: Bool = false
    public var modelLoadError: String? = nil
}
