import Combine
import SwiftUI

struct MainView: View {
    private let database: ScribbleFitDatabase
    private let networkClient: ScribbleFitNetworkClient
    @ObservedObject private var navManager: NavigationManager

    // DI-constructed dependencies
    private let canvasViewModel: CanvasViewModel
    private let ledgerViewModel: LedgerViewModel
    private let settingsViewModel: SettingsViewModel

    init(database: ScribbleFitDatabase, networkClient: ScribbleFitNetworkClient, navManager: NavigationManager) {
        self.database = database
        self.networkClient = networkClient
        self.navManager = navManager

        let secureKeyStorage = SecureKeyStorageImpl()
        let configRepository = ConfigRepositoryImpl(database: database)
        let dynamicEngine = DynamicLLMEngine(
            configRepository: configRepository,
            geminiEngine: GeminiAIEngine(
                networkClient: networkClient,
                secureKeyStorage: secureKeyStorage,
                prompt: SystemConfig.defaultPrompt
            ),
            openAIEngine: OpenAIAIEngine(
                networkClient: networkClient,
                secureKeyStorage: secureKeyStorage,
                prompt: SystemConfig.defaultPrompt
            ),
            localEngine: LocalAIEngine(),
            proxyEngine: ScribbleFitProxyEngine(
                networkClient: networkClient,
                prompt: SystemConfig.defaultPrompt
            )
        )

        let syncRepository = SyncRepositoryImpl(database: database)
        let syncWorkoutUseCase = SyncWorkoutUseCase(syncRepository: syncRepository, engine: dynamicEngine)
        syncRepository.configure(syncWorkoutUseCase: syncWorkoutUseCase)

        let ledgerRepository = LedgerRepositoryImpl(database: database)
        let canvasRepository = CanvasRepositoryImpl(syncRepository: syncRepository)
        let sessionRepository = WorkoutSessionRepositoryImpl(database: database)

        ledgerViewModel = LedgerViewModel(ledgerRepository: ledgerRepository)
        canvasViewModel = CanvasViewModel(
            canvasRepository: canvasRepository,
            processScribbleUseCase: ProcessScribbleUseCase(canvasRepository: canvasRepository),
            confirmWorkoutUseCase: ConfirmWorkoutUseCase(
                canvasRepository: canvasRepository,
                sessionRepository: sessionRepository,
                ledgerRepository: ledgerRepository
            ),
            executeQuickActionUseCase: ExecuteQuickActionUseCase(canvasRepository: canvasRepository)
        )

        let settingsRepo = SettingsRepositoryImpl(database: database)
        let modelRepo = ModelRepositoryImpl(networkClient: networkClient)
        settingsViewModel = SettingsViewModel(
            settingsRepository: settingsRepo,
            modelRepository: modelRepo,
            secureKeyStorage: secureKeyStorage
        )
    }

    var body: some View {
        TabView(selection: $navManager.selectedTab) {
            CanvasView(viewModel: canvasViewModel, onSettingsTap: { navManager.selectedTab = .profile })
                .tabItem { Label("Home", systemImage: "house") }
                .tag(AppTab.workout)

            LedgerView(viewModel: ledgerViewModel)
                .tabItem { Label("Log", systemImage: "list.bullet") }
                .tag(AppTab.ledger)

            ProfileView(settingsViewModel: settingsViewModel)
                .tabItem { Label("Settings", systemImage: "gearshape") }
                .tag(AppTab.profile)
        }
    }
}
