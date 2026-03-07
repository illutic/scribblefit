import Foundation
import SwiftUI
import Combine

@MainActor
public final class SettingsViewModel: ObservableObject {
    private let settingsRepository: SettingsRepository
    private let secureKeyStorage: SecureKeyStorage
    private let navManager: NavigationManager
    
    @Published public var uiState = SettingsUiState()
    
    public init(settingsRepository: SettingsRepository, secureKeyStorage: SecureKeyStorage, navManager: NavigationManager) {
        self.settingsRepository = settingsRepository
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
            } catch {
                print("Failed to load settings: \(error)")
                self.uiState.isLoading = false
            }
        }
    }
    
    public func updateParsingMode(_ mode: ParsingMode) {
        updateSettings { current in
            AppSettings(parsingMode: mode, aiProvider: current.aiProvider, weightUnit: current.weightUnit, themePreference: current.themePreference)
        }
    }
    
    public func updateApiKey(_ key: String) {
        uiState.apiKey = key
        Task {
            try? await secureKeyStorage.saveApiKey(key)
        }
    }
    
    public func updateProvider(_ provider: LLMProvider) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: provider, weightUnit: current.weightUnit, themePreference: current.themePreference)
        }
    }
    
    public func updateWeightUnit(_ unit: WeightUnit) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: current.aiProvider, weightUnit: unit, themePreference: current.themePreference)
        }
    }
    
    public func updateTheme(_ theme: ThemePreference) {
        updateSettings { current in
            AppSettings(parsingMode: current.parsingMode, aiProvider: current.aiProvider, weightUnit: current.weightUnit, themePreference: theme)
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
}
