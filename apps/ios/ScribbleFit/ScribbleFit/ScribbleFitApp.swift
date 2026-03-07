import SwiftUI
import SwiftData

@main
struct ScribbleFitApp: App {
    @StateObject private var appViewModel: AppViewModel
    
    // Dependencies
    private let syncRepository: SyncRepository
    private let ledgerRepository: LedgerRepository
    private let authRepository: AuthRepository
    private let configRepository: ConfigRepository
    private let analysisRepository: AnalysisRepository
    private let canvasRepository: CanvasRepository
    
    init() {
        // In a real app, we'd use a proper DI container
        let keychain = KeychainSecureKeyStorage()
        let network = ScribbleFitNetworkClient.shared
        let database = ScribbleFitDatabase.shared
        
        let syncRepo = SyncRepositoryImpl(database: database)
        let ledgerRepo = LedgerRepositoryImpl(database: database)
        let authRepo = AuthRepositoryImpl(networkClient: network, secureKeyStorage: keychain)
        let configRepo = ConfigRepositoryImpl(networkClient: network, database: database)
        let analysisRepo = AnalysisRepositoryImpl(database: database)
        let canvasRepo = CanvasRepositoryImpl(database: database)
        
        self.syncRepository = syncRepo
        self.ledgerRepository = ledgerRepo
        self.authRepository = authRepo
        self.configRepository = configRepo
        self.analysisRepository = analysisRepo
        self.canvasRepository = canvasRepo
        
        _appViewModel = StateObject(wrappedValue: AppViewModel(
            authRepository: authRepo,
            configRepository: configRepo
        ))
    }

    var body: some Scene {
        WindowGroup {
            Group {
                if appViewModel.isInitialized {
                    MainView(
                        canvasRepository: canvasRepository,
                        processScribbleUseCase: ProcessScribbleUseCase(canvasRepository: canvasRepository)
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
