import SwiftUI
import SwiftData
import FeatureConfig
import CoreModel
import CoreFirebase

@main
struct ScribbleFitApp: App {
    @StateObject private var appViewModel: AppViewModel

    // Dependencies
    private let syncRepository: SyncRepository
    private let ledgerRepository: LedgerRepository
    private let configRepository: ConfigRepository
    private let analysisRepository: AnalysisRepository
    private let canvasRepository: CanvasRepository
    private let userRepository: UserRepository
    private let settingsRepository: SettingsRepository
    private let modelRepository: ModelRepository
    private let database: ScribbleFitDatabase

    init() {
        // In a real app, we'd use a proper DI container
        let network = ScribbleFitNetworkClient.shared
        let database = ScribbleFitDatabase.shared
        self.database = database

        let syncRepo = SyncRepositoryImpl(database: database)
        let ledgerRepo = LedgerRepositoryImpl(database: database)
        let configRepo = ConfigRepositoryImpl(database: database)
        let analysisRepo = AnalysisRepositoryImpl(database: database)

        // AI Engines
        let geminiAIEngine = GeminiAIEngine(apiKey: "", configRepository: configRepo)
        let localAIEngine = LocalLLMService(configRepository: configRepo)

        let dynamicEngine = DynamicLLMEngine(
            geminiService: geminiAIEngine,
            localService: localAIEngine,
            configRepository: configRepo
        )

        let syncWorkoutUseCase = SyncWorkoutUseCase(
            syncRepository: syncRepo,
            engine: dynamicEngine,
            configRepository: configRepo
        )

        // Wire up circular dependency lazily
        syncRepo.setSyncWorkoutUseCase(syncWorkoutUseCase)

        let canvasRepo = CanvasRepositoryImpl(syncRepository: syncRepo)
        let userRepo = UserRepositoryImpl(ledgerRepository: ledgerRepo)
        let settingsRepo = SettingsRepositoryImpl(database: database)
        let modelRepo = ModelRepositoryImpl()

        self.syncRepository = syncRepo
        self.ledgerRepository = ledgerRepo
        self.configRepository = configRepo
        self.analysisRepository = analysisRepo
        self.canvasRepository = canvasRepo
        self.userRepository = userRepo
        self.settingsRepository = settingsRepo
        self.modelRepository = modelRepo

        _appViewModel = StateObject(wrappedValue: AppViewModel(
            configRepository: configRepo
        ))
    }

    var body: some Scene {
        WindowGroup {
            Group {
                if appViewModel.isInitialized {
                    MainView(
                        canvasRepository: canvasRepository,
                        analysisRepository: analysisRepository,
                        userRepository: userRepository,
                        settingsRepository: settingsRepository,
                        modelRepository: modelRepository,
                        processScribbleUseCase: ProcessScribbleUseCase(canvasRepository: canvasRepository),
                        executeQuickActionUseCase: ExecuteQuickActionUseCase(canvasRepository: canvasRepository),
                        confirmWorkoutUseCase: ConfirmWorkoutUseCase(sessionRepository: WorkoutSessionRepositoryImpl(database: database), ledgerRepository: ledgerRepository),
                        listenForSyncItemsUseCase: ListenForSyncItemsUseCase(syncRepository: syncRepository)
                    )
                } else {
                    SplashScreenView()
                }
            }
            .onAppear {
                appViewModel.initialize()
            }
        }
    }
}

struct SplashScreenView: View {
    var body: some View {
        ZStack {
            ScribbleFitColor.background.ignoresSafeArea()

            VStack(spacing: 16) {
                Text("ScribbleFit")
                    .font(.system(size: 40, weight: .black))
                    .foregroundColor(ScribbleFitColor.primaryText)
                    .tracking(-2)

                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: ScribbleFitColor.primaryText))
            }
        }
    }
}
