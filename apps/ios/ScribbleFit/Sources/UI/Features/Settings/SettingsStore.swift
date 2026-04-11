import Foundation
import Observation
import Combine

@Observable
@MainActor
public final class SettingsStore {
    public var state = SettingsState()
    
    private let configRepository: ConfigRepository
    private let settingsRepository: SettingsRepository
    private let updateApiKeyUseCase: UpdateApiKeyUseCase
    private let testConnectionUseCase: TestConnectionUseCase
    private let getAvailableModelsUseCase: GetAvailableModelsUseCase
    private let checkLocalSupportUseCase: CheckLocalSupportUseCase
    private let clearAllDataUseCase: ClearAllDataUseCase
    private let exportUserDataUseCase: ExportUserDataUseCase
    
    private var cancellables = Set<AnyCancellable>()

    public init(
        configRepository: ConfigRepository,
        settingsRepository: SettingsRepository,
        updateApiKeyUseCase: UpdateApiKeyUseCase,
        testConnectionUseCase: TestConnectionUseCase,
        getAvailableModelsUseCase: GetAvailableModelsUseCase,
        checkLocalSupportUseCase: CheckLocalSupportUseCase,
        clearAllDataUseCase: ClearAllDataUseCase,
        exportUserDataUseCase: ExportUserDataUseCase
    ) {
        self.configRepository = configRepository
        self.settingsRepository = settingsRepository
        self.updateApiKeyUseCase = updateApiKeyUseCase
        self.testConnectionUseCase = testConnectionUseCase
        self.getAvailableModelsUseCase = getAvailableModelsUseCase
        self.checkLocalSupportUseCase = checkLocalSupportUseCase
        self.clearAllDataUseCase = clearAllDataUseCase
        self.exportUserDataUseCase = exportUserDataUseCase
        
        loadInitialData()
    }

    private func loadInitialData() {
        state.config = configRepository.getConfig()
        
        Task {
            state.isLocalLlmSupported = await checkLocalSupportUseCase.execute()
            
            if let apiKey = try? await settingsRepository.getApiKey() {
                state.apiKey = apiKey
                if !apiKey.isEmpty {
                    fetchAvailableModels(apiKey: apiKey)
                }
            }
        }
        
        configRepository.configPublisher
            .receive(on: RunLoop.main)
            .sink { [weak self] config in
                self?.state.config = config
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
            
        case .updateApiKey(let key):
            state.apiKey = key
            Task {
                try? await updateApiKeyUseCase.execute(apiKey: key)
            }
            
        case .toggleApiKeyVisibility:
            state.isApiKeyVisible.toggle()
            
        case .testConnection:
            testConnection()
            
        case .fetchAvailableModels:
            fetchAvailableModels(apiKey: state.apiKey)
            
        case .updatePreferredModel(let model):
            var newConfig = state.config
            newConfig.preferredModel = model
            configRepository.updateConfig(newConfig)
            
        case .exportData:
            exportData()
            
        case .clearAllData:
            clearData()
            
        case .showClearConfirmation(let show):
            state.isShowingClearConfirmation = show
            
        case .dismissError:
            state.error = nil
        }
    }

    private func testConnection() {
        guard !state.apiKey.isEmpty else { return }
        
        state.isTestingConnection = true
        state.connectionTestResult = nil
        
        Task {
            do {
                try await testConnectionUseCase.execute(apiKey: state.apiKey)
                state.connectionTestResult = .success
                fetchAvailableModels(apiKey: state.apiKey)
            } catch {
                state.connectionTestResult = .failure(error.localizedDescription)
            }
            state.isTestingConnection = false
        }
    }

    private func fetchAvailableModels(apiKey: String) {
        guard !apiKey.isEmpty else { return }
        
        state.isLoadingModels = true
        
        Task {
            do {
                let models = try await getAvailableModelsUseCase.execute(apiKey: apiKey)
                state.availableModels = models
                
                // Set default model if none selected
                if state.config.preferredModel == nil || !models.contains(state.config.preferredModel!) {
                    if let first = models.first {
                        var newConfig = state.config
                        newConfig.preferredModel = first
                        configRepository.updateConfig(newConfig)
                    }
                }
            } catch {
                print("Failed to fetch models: \(error)")
            }
            state.isLoadingModels = false
        }
    }

    private func exportData() {
        state.isExporting = true
        Task {
            do {
                let url = try await exportUserDataUseCase.execute()
                state.exportURL = url
            } catch {
                state.error = "Failed to export data: \(error.localizedDescription)"
            }
            state.isExporting = false
        }
    }

    private func clearData() {
        Task {
            do {
                try await clearAllDataUseCase.execute()
                state.apiKey = ""
                state.availableModels = []
                state.isShowingClearConfirmation = false
                // App will react to data changes
            } catch {
                state.error = "Failed to clear data: \(error.localizedDescription)"
            }
        }
    }
}
