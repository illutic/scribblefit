import Foundation
import CoreModel

public struct SettingsState: Equatable, Sendable {
    public var config: SystemConfig = SystemConfig()
    public var isLocalLlmSupported: Bool = true
    public var isExporting: Bool = false
    public var exportURL: URL? = nil
    public var isShowingClearConfirmation: Bool = false
    public var error: String? = nil
}
