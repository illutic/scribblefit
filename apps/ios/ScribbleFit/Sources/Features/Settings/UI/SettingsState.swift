import Foundation
import CoreModel

public struct SettingsState: Equatable, Sendable {
    public var config: SystemConfig = SystemConfig()
    public var apiKey: String = ""
    public var isApiKeyVisible: Bool = false
    public var isTestingConnection: Bool = false
    public var connectionTestResult: ConnectionTestResult? = nil
    public var availableModels: [String] = []
    public var isLoadingModels: Bool = false
    public var isLocalLlmSupported: Bool = true
    public var isExporting: Bool = false
    public var exportURL: URL? = nil
    public var isShowingClearConfirmation: Bool = false
    public var error: String? = nil
    
    public enum ConnectionTestResult: Equatable, Sendable {
        case success
        case failure(String)
    }
}
