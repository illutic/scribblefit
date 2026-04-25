import Foundation
import CoreModel

public enum SettingsIntent: Sendable {
    case updateTheme(ThemePreference)
    case updateWeightUnit(WeightUnit)
    case updateLlmProvider(LLMProvider)
    case exportData
    case clearAllData
    case showClearConfirmation(Bool)
    case dismissError
}
