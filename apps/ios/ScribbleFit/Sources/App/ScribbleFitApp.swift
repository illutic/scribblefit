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
import FirebaseCore

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
    private let getExerciseTrendsUseCase: GetExerciseTrendDataUseCase
    private let getExerciseHistoryUseCase: GetExerciseHistoryUseCase
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
            let getScribblesForDateUC = GetScribblesForDateUseCase(repository: scribbleRepo)
            self.getScribblesForDateUseCase = getScribblesForDateUC

            let addRawScribbleUC = AddRawScribbleUseCase(repository: scribbleRepo)
            self.addRawScribbleUseCase = addRawScribbleUC

            let removeScribbleUC = RemoveScribbleUseCase(repository: scribbleRepo)
            let confirmScribbleUC = ConfirmScribbleUseCase(scribbleRepository: scribbleRepo)
            self.confirmScribbleUseCase = confirmScribbleUC

            let deleteScribbleUC = DeleteScribbleUseCase(removeScribbleUseCase: removeScribbleUC)
            self.deleteScribbleUseCase = deleteScribbleUC

            let parsePendingScribblesUC = ParsePendingScribblesUseCase(scribbleRepository: scribbleRepo, llmProvider: routingLLM)
            self.parsePendingScribblesUseCase = parsePendingScribblesUC

            let getAIOverviewUC = GetAIOverviewUseCase(scribbleRepository: scribbleRepo, llmProvider: routingLLM)
            self.getAIOverviewUseCase = getAIOverviewUC

            let clearAllDataUC = ClearAllDataUseCase(repository: settingsRepo)
            self.clearAllDataUseCase = clearAllDataUC

            let checkLocalSupportUC = CheckLocalSupportUseCase(localLLM: localLLM)
            self.checkLocalSupportUseCase = checkLocalSupportUC

            let exportUserDataUC = ExportUserDataUseCase(repository: settingsRepo)
            self.exportUserDataUseCase = exportUserDataUC

            let getVolumeInsightsUC = GetVolumeInsightsUseCase(scribbleRepository: scribbleRepo)
            self.getVolumeInsightsUseCase = getVolumeInsightsUC

            let getFrequencyInsightsUC = GetFrequencyInsightsUseCase(scribbleRepository: scribbleRepo)
            self.getFrequencyInsightsUseCase = getFrequencyInsightsUC

            let getMuscleDistributionInsightsUC = GetMuscleDistributionInsightsUseCase(scribbleRepository: scribbleRepo)
            self.getMuscleDistributionInsightsUseCase = getMuscleDistributionInsightsUC

            let reorderSetsUC = ReorderSetsUseCase()
            self.reorderSetsUseCase = reorderSetsUC

            let manualEditScribbleUC = ManualEditScribbleUseCase(scribbleRepository: scribbleRepo)
            self.manualEditScribbleUseCase = manualEditScribbleUC

            let createManualScribbleUC = CreateManualScribbleUseCase(scribbleRepository: scribbleRepo)
            self.createManualScribbleUseCase = createManualScribbleUC

            let getExerciseDetailsUC = GetExerciseDetailsUseCase(scribbleRepository: scribbleRepo)
            self.getExerciseDetailsUseCase = getExerciseDetailsUC

            let getExerciseAIInsightUC = GetExerciseAIInsightUseCase(llmService: routingLLM)
            self.getExerciseAIInsightUseCase = getExerciseAIInsightUC

            let getExerciseTrendsUC = GetExerciseTrendDataUseCase(exerciseRepository: exerciseRepo)
            self.getExerciseTrendsUseCase = getExerciseTrendsUC

            let formatExerciseSummaryUC = FormatExerciseSummaryUseCase()
            let getExerciseHistoryUC = GetExerciseHistoryUseCase(
                exerciseRepository: exerciseRepo,
                formatExerciseSummaryUseCase: formatExerciseSummaryUC
            )
            self.getExerciseHistoryUseCase = getExerciseHistoryUC

            self.addManualExerciseUseCase = AddManualExerciseUseCase(exerciseRepository: exerciseRepo)
            self.addSetToExerciseUseCase = AddSetToExerciseUseCase(exerciseRepository: exerciseRepo)

            let calculateTrendsUC = CalculateTrendsUseCase(exerciseRepository: exerciseRepo)
            self.calculateTrendsUseCase = calculateTrendsUC

            // Stores
            self.canvasStore = CanvasStore(
                getScribblesForDateUseCase: getScribblesForDateUC,
                addRawScribbleUseCase: addRawScribbleUC,
                confirmScribbleUseCase: confirmScribbleUC,
                deleteScribbleUseCase: deleteScribbleUC,
                parsePendingScribblesUseCase: parsePendingScribblesUC,
                getAIOverviewUseCase: getAIOverviewUC,
                manualEditScribbleUseCase: manualEditScribbleUC,
                createManualScribbleUseCase: createManualScribbleUC,
                reorderSetsUseCase: reorderSetsUC,
                calculateTrendsUseCase: calculateTrendsUC,
                configRepository: configRepo
            )

            self.settingsStore = SettingsStore(
                configRepository: configRepo,
                settingsRepository: settingsRepo,
                checkLocalSupportUseCase: checkLocalSupportUC,
                clearAllDataUseCase: clearAllDataUC,
                exportUserDataUseCase: exportUserDataUC
            )

            self.insightsStore = InsightsStore(
                getAIOverviewUseCase: getAIOverviewUC,
                getVolumeInsightsUseCase: getVolumeInsightsUC,
                getFrequencyInsightsUseCase: getFrequencyInsightsUC,
                getMuscleDistributionInsightsUseCase: getMuscleDistributionInsightsUC,
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
                getExerciseTrendsUseCase: getExerciseTrendsUseCase,
                getExerciseHistoryUseCase: getExerciseHistoryUseCase,
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
    let getExerciseTrendsUseCase: GetExerciseTrendDataUseCase
    let getExerciseHistoryUseCase: GetExerciseHistoryUseCase
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
                getExerciseTrendsUseCase: getExerciseTrendsUseCase,
                getExerciseHistoryUseCase: getExerciseHistoryUseCase,
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
                getExerciseTrendsUseCase: getExerciseTrendsUseCase,
                getExerciseHistoryUseCase: getExerciseHistoryUseCase,
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
        .preferredColorScheme(settingsStore.state.config.themePreference.getColorScheme())
        .onReceive(NotificationCenter.default.publisher(for: NSNotification.Name("NavigateToCanvasDate"))) { notification in
            if let date = notification.object as? Date {
                selectedTab = 0
                canvasStore.onIntent(.dismissDetails)
                ledgerStore.onIntent(.dismissDetails)
                canvasStore.onIntent(.onDateSelected(date))
            }
        }
    }
}
