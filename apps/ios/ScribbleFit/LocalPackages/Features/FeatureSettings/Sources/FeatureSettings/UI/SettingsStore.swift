import Foundation
import Observation
import Combine
import CoreModel

@Observable
@MainActor
public final class SettingsStore {
    public var state = SettingsState()

    private let configRepository: ConfigRepository
    private let settingsRepository: SettingsRepository
    private let checkLocalSupportUseCase: CheckLocalSupportUseCase
    private let clearAllDataUseCase: ClearAllDataUseCase
    private let exportUserDataUseCase: ExportUserDataUseCase

    private var cancellables = Set<AnyCancellable>()

    public init(
        configRepository: ConfigRepository,
        settingsRepository: SettingsRepository,
        checkLocalSupportUseCase: CheckLocalSupportUseCase,
        clearAllDataUseCase: ClearAllDataUseCase,
        exportUserDataUseCase: ExportUserDataUseCase
    ) {
        self.configRepository = configRepository
        self.settingsRepository = settingsRepository
        self.checkLocalSupportUseCase = checkLocalSupportUseCase
        self.clearAllDataUseCase = clearAllDataUseCase
        self.exportUserDataUseCase = exportUserDataUseCase

        loadInitialData()
    }

    private func loadInitialData() {
        state = state.copy(config: configRepository.getConfig())

        if let version = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String,
           let build = Bundle.main.infoDictionary?["CFBundleVersion"] as? String {
            state = state.copy(version: "\(version) (\(build))")
        }

        Task {
            let isSupported = await checkLocalSupportUseCase.execute()
            state = state.copy(isLocalLlmSupported: isSupported)
        }

        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state = self?.state.copy(config: config) ?? .init()
            }
            .store(in: &cancellables)
    }

    public func onIntent(_ intent: SettingsIntent) {
        switch intent {
        case .updateTheme(let theme):
            var newConfig = state.config
            newConfig.themePreference = theme
            configRepository.updateConfig(newConfig)

        case .updateWeightUnit(let unit):
            var newConfig = state.config
            newConfig.weightUnit = unit
            configRepository.updateConfig(newConfig)

        case .updateLlmProvider(let provider):
            var newConfig = state.config
            newConfig.preferredLlmProvider = provider
            configRepository.updateConfig(newConfig)

        case .exportData:
            exportData()

        case .clearAllData:
            clearData()

        case .showClearConfirmation(let show):
            state = state.copy(isShowingClearConfirmation: show)

        case .dismissError:
            state = state.copy(error: .some(nil))
        }
    }

    private func exportData() {
        state = state.copy(isExporting: true)
        Task {
            do {
                let url = try await exportUserDataUseCase.execute()
                state = state.copy(exportURL: url)
            } catch {
                state = state.copy(error: "Failed to export data: \(error.localizedDescription)")
            }
            state = state.copy(isExporting: false)
        }
    }

    private func clearData() {
        Task {
            do {
                try await clearAllDataUseCase.execute()
                state = state.copy(isShowingClearConfirmation: false)
            } catch {
                state = state.copy(error: "Failed to clear data: \(error.localizedDescription)")
            }
        }
    }
}
