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
    
    init() {
        // In a real app, we'd use a proper DI container
        let keychain = KeychainSecureKeyStorage()
        let network = ScribbleFitNetworkClient.shared
        let database = ScribbleFitDatabase.shared
        
        let syncRepo = SyncRepositoryImpl(database: database)
        let ledgerRepo = LedgerRepositoryImpl(database: database)
        let authRepo = AuthRepositoryImpl(networkClient: network, secureKeyStorage: keychain)
        let configRepo = ConfigRepositoryImpl(networkClient: network, database: database)
        
        self.syncRepository = syncRepo
        self.ledgerRepository = ledgerRepo
        self.authRepository = authRepo
        self.configRepository = configRepo
        
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
                        syncRepository: syncRepository,
                        ledgerRepository: ledgerRepository
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
            Color.white.ignoresSafeArea()
            
            VStack(spacing: 16) {
                Text("ScribbleFit")
                    .font(.system(size: 40, weight: .black))
                    .foregroundColor(Color(hex: "101010"))
                    .tracking(-2)
                
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: Color(hex: "101010")))
            }
        }
    }
}
