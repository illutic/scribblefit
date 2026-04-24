import SwiftUI
import SwiftData
import CoreFirebase
import CoreModel
import CoreDatabase
import CoreDesignSystem
import CoreCommon
import FeatureAI
import FeatureScribble
import FeatureConfig
import FeatureCanvas
import FeatureSettings
import FeatureInsights
import FeatureLedger
import FeatureSets
import FeatureExercises
import FirebaseAppCheck
import FirebaseAuth

class ScribbleFitAppCheckProviderFactory: NSObject, AppCheckProviderFactory {
    func createProvider(with app: FirebaseApp) -> AppCheckProvider? {
        #if DEBUG
        return AppCheckDebugProvider(app: app)
        #else
        return AppAttestProvider(app: app)
        #endif
    }
}

@main
@MainActor
struct ScribbleFitApp: App {
    private let modelContainer: ModelContainer

    // Repositories
    private let scribbleRepository: ScribbleRepository
    private let configRepository: ConfigRepository
    private let settingsRepository: SettingsRepository
    private let exerciseRepository: ExerciseRepository

    // Providers
    private let llmProvider: LLMService

    // Use Cases
    private let getScribblesForDateUseCase: GetScribblesForDateUseCase
    private let addRawScribbleUseCase: AddRawScribbleUseCase
    private let confirmScribbleUseCase: ConfirmScribbleUseCase
    private let deleteScribbleUseCase: DeleteScribbleUseCase
    private let parsePendingScribblesUseCase: ParsePendingScribblesUseCase
    private let getAIOverviewUseCase: GetAIOverviewUseCase
    private let clearAllDataUseCase: ClearAllDataUseCase
    private let checkLocalSupportUseCase: CheckLocalSupportUseCase
    private let exportUserDataUseCase: ExportUserDataUseCase
    private let getVolumeInsightsUseCase: GetVolumeInsightsUseCase
    private let getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase
    private let manualEditScribbleUseCase: ManualEditScribbleUseCase
    private let createManualScribbleUseCase: CreateManualScribbleUseCase
    private let reorderSetsUseCase: ReorderSetsUseCase
    private let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    private let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    private let addManualExerciseUseCase: AddManualExerciseUseCase
    private let addSetToExerciseUseCase: AddSetToExerciseUseCase
    private let calculateTrendsUseCase: CalculateTrendsUseCase

    // Stores
    private let canvasStore: CanvasStore
    private let settingsStore: SettingsStore
    private let insightsStore: InsightsStore
    private let ledgerStore: LedgerStore

    init() {
        let providerFactory = ScribbleFitAppCheckProviderFactory()
        AppCheck.setAppCheckProviderFactory(providerFactory)
        FirebaseApp.configure()
        
        // Sign in anonymously to provide auth context for Gemini / App Check
        Auth.auth().signInAnonymously { _, error in
            if let error = error {
                print("Firebase Anonymous Auth Error: \(error.localizedDescription)")
            } else {
                print("Firebase Anonymous Auth Success")
            }
        }

        do {
            let schema = Schema([
                ScribbleEntity.self,
                ExerciseEntity.self,
                SetEntity.self
            ])

            modelContainer = try ModelContainer(for: schema)

            // Repositories
            let scribbleRepo = ScribbleRepositoryImpl(modelContainer: modelContainer)
            let configRepo = ConfigRepositoryImpl()
            let settingsRepo = SettingsRepositoryImpl(modelContainer: modelContainer)
            let exerciseRepo = ExerciseRepositoryImpl(modelContainer: modelContainer)

            self.scribbleRepository = scribbleRepo
            self.configRepository = configRepo
            self.settingsRepository = settingsRepo
            self.exerciseRepository = exerciseRepo

            // AI Services
            let localLLM = LocalLLMService(configRepository: configRepo)
            let geminiLLM = GeminiLLMService(configRepository: configRepo, settingsRepository: settingsRepo)
            let routingLLM = RoutingLLMService(geminiService: geminiLLM, localService: localLLM, configRepository: configRepo)
            self.llmProvider = routingLLM

            // Use Cases
            self.getScribblesForDateUseCase = GetScribblesForDateUseCase(repository: scribbleRepo)
            self.addRawScribbleUseCase = AddRawScribbleUseCase(repository: scribbleRepo)
            let removeScribbleUC = RemoveScribbleUseCase(repository: scribbleRepo)
            self.confirmScribbleUseCase = ConfirmScribbleUseCase(scribbleRepository: scribbleRepo)
            self.deleteScribbleUseCase = DeleteScribbleUseCase(removeScribbleUseCase: removeScribbleUC)
            self.parsePendingScribblesUseCase = ParsePendingScribblesUseCase(scribbleRepository: scribbleRepo, llmProvider: routingLLM)
            self.getAIOverviewUseCase = GetAIOverviewUseCase(scribbleRepository: scribbleRepo, llmProvider: routingLLM)

            self.clearAllDataUseCase = ClearAllDataUseCase(repository: settingsRepo)
            self.checkLocalSupportUseCase = CheckLocalSupportUseCase(localLLM: localLLM)
            self.exportUserDataUseCase = ExportUserDataUseCase(repository: settingsRepo)
            
            self.getVolumeInsightsUseCase = GetVolumeInsightsUseCase(scribbleRepository: scribbleRepo)
            self.getFrequencyInsightsUseCase = GetFrequencyInsightsUseCase(scribbleRepository: scribbleRepo)
            self.getMuscleDistributionInsightsUseCase = GetMuscleDistributionInsightsUseCase(scribbleRepository: scribbleRepo)

            let reorderSetsUC = ReorderSetsUseCase()
            self.reorderSetsUseCase = reorderSetsUC
            
            let manualEditScribbleUC = ManualEditScribbleUseCase(scribbleRepository: scribbleRepo)
            self.manualEditScribbleUseCase = manualEditScribbleUC
            self.createManualScribbleUseCase = CreateManualScribbleUseCase(scribbleRepository: scribbleRepo)

            self.getExerciseDetailsUseCase = GetExerciseDetailsUseCase(scribbleRepository: scribbleRepo)
            self.getExerciseAIInsightUseCase = GetExerciseAIInsightUseCase(llmService: routingLLM)

            self.addManualExerciseUseCase = AddManualExerciseUseCase(exerciseRepository: exerciseRepo)
            self.addSetToExerciseUseCase = AddSetToExerciseUseCase(exerciseRepository: exerciseRepo)
            self.calculateTrendsUseCase = CalculateTrendsUseCase(exerciseRepository: exerciseRepo)

            // Stores
            self.canvasStore = CanvasStore(
                getScribblesForDateUseCase: getScribblesForDateUseCase,
                addRawScribbleUseCase: addRawScribbleUseCase,
                confirmScribbleUseCase: confirmScribbleUseCase,
                deleteScribbleUseCase: deleteScribbleUseCase,
                parsePendingScribblesUseCase: parsePendingScribblesUseCase,
                getAIOverviewUseCase: getAIOverviewUseCase,
                manualEditScribbleUseCase: manualEditScribbleUC,
                createManualScribbleUseCase: self.createManualScribbleUseCase,
                reorderSetsUseCase: reorderSetsUC,
                calculateTrendsUseCase: self.calculateTrendsUseCase,
                configRepository: configRepo
            )

            self.settingsStore = SettingsStore(
                configRepository: configRepo,
                settingsRepository: settingsRepo,
                checkLocalSupportUseCase: checkLocalSupportUseCase,
                clearAllDataUseCase: clearAllDataUseCase,
                exportUserDataUseCase: exportUserDataUseCase
            )

            self.insightsStore = InsightsStore(
                getAIOverviewUseCase: getAIOverviewUseCase,
                getVolumeInsightsUseCase: getVolumeInsightsUseCase,
                getFrequencyInsightsUseCase: getFrequencyInsightsUseCase,
                getMuscleDistributionInsightsUseCase: getMuscleDistributionInsightsUseCase,
                configRepository: configRepo
            )

            self.ledgerStore = LedgerStore(
                scribbleRepository: scribbleRepo
            )

        } catch {
            print("ERROR INITIALIZING SWIFTDATA: \(error)")
            fatalError("Could not initialize SwiftData: \(error)")
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView(
                canvasStore: canvasStore,
                settingsStore: settingsStore,
                insightsStore: insightsStore,
                ledgerStore: ledgerStore,
                getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                configRepository: configRepository,
                exerciseRepository: exerciseRepository,
                manualEditScribbleUseCase: manualEditScribbleUseCase,
                createManualScribbleUseCase: createManualScribbleUseCase,
                addManualExerciseUseCase: addManualExerciseUseCase,
                addSetToExerciseUseCase: addSetToExerciseUseCase,
                scribbleRepository: scribbleRepository,
                confirmScribbleUseCase: confirmScribbleUseCase
            )
        }
        .modelContainer(modelContainer)
    }
}

struct ContentView: View {
    @Bindable var canvasStore: CanvasStore
    @Bindable var settingsStore: SettingsStore
    @Bindable var insightsStore: InsightsStore
    @Bindable var ledgerStore: LedgerStore
    
    let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    let configRepository: ConfigRepository
    let exerciseRepository: ExerciseRepository
    let manualEditScribbleUseCase: ManualEditScribbleUseCase
    let createManualScribbleUseCase: CreateManualScribbleUseCase
    let addManualExerciseUseCase: AddManualExerciseUseCase
    let addSetToExerciseUseCase: AddSetToExerciseUseCase
    let scribbleRepository: ScribbleRepository
    let confirmScribbleUseCase: ConfirmScribbleUseCase

    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            CanvasView(
                store: canvasStore,
                settingsStore: settingsStore,
                getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                configRepository: configRepository,
                exerciseRepository: exerciseRepository,
                manualEditScribbleUseCase: manualEditScribbleUseCase,
                createManualScribbleUseCase: createManualScribbleUseCase,
                addManualExerciseUseCase: addManualExerciseUseCase,
                addSetToExerciseUseCase: addSetToExerciseUseCase,
                scribbleRepository: scribbleRepository,
                confirmScribbleUseCase: confirmScribbleUseCase
            )
            .tabItem {
                Label(String(localized: "Today"), systemImage: "house.fill")
            }
            .tag(0)

            InsightsView(store: insightsStore)
                .tabItem {
                    Label(String(localized: "Insights"), systemImage: "star.fill")
                }
                .tag(1)

            LedgerView(
                store: ledgerStore,
                onNavigateToCanvas: { selectedTab = 0 },
                getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                configRepository: configRepository,
                exerciseRepository: exerciseRepository,
                addManualExerciseUseCase: addManualExerciseUseCase,
                addSetToExerciseUseCase: addSetToExerciseUseCase,
                scribbleRepository: scribbleRepository,
                confirmScribbleUseCase: confirmScribbleUseCase
            )
            .tabItem {
                Label(String(localized: "Ledger"), systemImage: "person.fill")
            }
            .tag(2)
        }
        .tint(Color.scribblePrimary)
        .preferredColorScheme(colorScheme)
    }

    private var colorScheme: ColorScheme? {
        switch settingsStore.state.config.themePreference {
        case .light: return .light
        case .dark: return .dark
        case .system: return nil
        }
    }
}
