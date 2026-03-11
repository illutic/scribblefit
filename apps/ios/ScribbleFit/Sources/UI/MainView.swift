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
                prompt: SystemConfigDomain.PARSE_PROMPT
            ),
            openAIEngine: OpenAIAIEngine(
                networkClient: networkClient,
                secureKeyStorage: secureKeyStorage,
                prompt: SystemConfigDomain.PARSE_PROMPT
            ),
            localEngine: LocalAIEngine(),
            proxyEngine: ScribbleFitProxyEngine(
                networkClient: networkClient,
                prompt: SystemConfigDomain.PARSE_PROMPT
            )
        )

        let ledgerRepository = LedgerRepositoryImpl(database: database)
        let scribbleRepository = ScribbleRepositoryImpl(database: database)

        ledgerViewModel = LedgerViewModel(ledgerRepository: ledgerRepository)
        canvasViewModel = CanvasViewModel(
            scribbleRepository: scribbleRepository,
            confirmWorkoutUseCase: ConfirmWorkoutUseCase(
                scribbleRepository: scribbleRepository,
                ledgerRepository: ledgerRepository
            )
        )

        let modelRepo = ModelRepositoryImpl(networkClient: networkClient)
        settingsViewModel = SettingsViewModel(
            configRepository: configRepository,
            modelRepository: modelRepo,
            secureKeyStorage: secureKeyStorage
        )

        // Start background sync
        let syncScribblesUseCase = SyncScribblesUseCase(
            scribbleRepository: scribbleRepository,
            engine: dynamicEngine
        )
        Task {
            await syncScribblesUseCase.execute()
        }
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
