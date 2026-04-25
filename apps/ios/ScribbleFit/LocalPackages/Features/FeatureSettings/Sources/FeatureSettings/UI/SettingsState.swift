import Foundation
import CoreModel

public struct SettingsState: Equatable, Sendable {
    public var config: SystemConfig = SystemConfig()
    public var isLocalLlmSupported: Bool = true
    public var isExporting: Bool = false
    public var exportURL: URL? = nil
    public var isShowingClearConfirmation: Bool = false
    public var error: String? = nil

    public init() {}

    public func copy(
        config: SystemConfig? = nil,
        isLocalLlmSupported: Bool? = nil,
        isExporting: Bool? = nil,
        exportURL: URL?? = nil,
        isShowingClearConfirmation: Bool? = nil,
        error: String?? = nil
    ) -> SettingsState {
        var newState = self
        if let config = config { newState.config = config }
        if let isLocalLlmSupported = isLocalLlmSupported { newState.isLocalLlmSupported = isLocalLlmSupported }
        if let isExporting = isExporting { newState.isExporting = isExporting }
        if let exportURL = exportURL { newState.exportURL = exportURL }
        if let isShowingClearConfirmation = isShowingClearConfirmation { newState.isShowingClearConfirmation = isShowingClearConfirmation }
        if let error = error { newState.error = error }
        return newState
    }
}
