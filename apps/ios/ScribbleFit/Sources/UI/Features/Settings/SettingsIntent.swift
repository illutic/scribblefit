import Foundation

public enum SettingsIntent: Sendable {
    case updateTheme(ThemePreference)
    case updateWeightUnit(WeightUnit)
    case updateLlmProvider(LLMProvider)
    case updateApiKey(String)
    case toggleApiKeyVisibility
    case testConnection
    case fetchAvailableModels
    case updatePreferredModel(String)
    case exportData
    case clearAllData
    case showClearConfirmation(Bool)
    case dismissError
}
