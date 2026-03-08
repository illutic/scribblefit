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
        let geminiEngine = GeminiAIEngine(
            networkClient: networkClient,
            secureKeyStorage: secureKeyStorage,
            prompt: SystemConfig.defaultPrompt
        )

        let syncWorkoutUseCase = SyncWorkoutUseCase(syncRepository: PlaceholderSyncRepository(), engine: geminiEngine)
        let syncRepository = SyncRepositoryImpl(database: database, syncWorkoutUseCase: syncWorkoutUseCase)

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
            CanvasView(viewModel: canvasViewModel)
                .tabItem { Label("Home", systemImage: "house") }
                .tag(AppTab.workout)

            LedgerView(viewModel: ledgerViewModel)
                .tabItem { Label("Log", systemImage: "list.bullet") }
                .tag(AppTab.ledger)

            ProfileView(settingsViewModel: settingsViewModel)
                .tabItem { Label("Profile", systemImage: "person") }
                .tag(AppTab.profile)
        }
    }
}

// Temporary placeholder to break the circular init dependency in SyncRepositoryImpl
private final class PlaceholderSyncRepository: SyncRepository {
    func getPendingSyncItems() async throws -> [AISyncItem] { [] }
    func getAllSyncItems() async throws -> [AISyncItem] { [] }
    func observeAllSyncItems() -> AnyPublisher<[AISyncItem], Never> { Just([]).eraseToAnyPublisher() }
    func updateSyncStatus(id: String, status: SyncStatus) async throws {}
    func saveParsedWorkout(syncItemId: String, workout: ParsedWorkout) async throws {}
    func enqueueScribble(id: String, rawText: String) async throws {}
    func saveFeedItem(id: String, type: String, jsonData: String, status: SyncStatus) async throws {}
    func deleteSyncItem(id: String) async throws {}
    func syncWorkouts() async throws {}
}
