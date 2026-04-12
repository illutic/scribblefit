import SwiftUI
import SwiftData
#if SWIFT_PACKAGE
import CoreModel
import CoreDatabase
import CoreDesignSystem
import CoreCommon
import FeatureAI
import FeatureScribble
import FeatureWorkouts
import FeatureConfig
import FeatureCanvas
import FeatureSettings
import FeatureInsights
import FeatureLedger
#endif

@main
@MainActor
struct ScribbleFitApp: App {
    private let modelContainer: ModelContainer

    // Repositories
    private let scribbleRepository: ScribbleRepository
    private let configRepository: ConfigRepository
    private let workoutRepository: WorkoutRepository
    private let settingsRepository: SettingsRepository

    // Providers
    private let llmProvider: LLMService

    // Use Cases
    private let getScribblesForDateUseCase: GetScribblesForDateUseCase
    private let addRawScribbleUseCase: AddRawScribbleUseCase
    private let confirmScribbleUseCase: ConfirmScribbleUseCase
    private let deleteScribbleUseCase: DeleteScribbleUseCase
    private let parsePendingScribblesUseCase: ParsePendingScribblesUseCase
    private let getAIOverviewUseCase: GetAIOverviewUseCase
    private let getWorkoutsByDateUseCase: GetWorkoutsByDateUseCase
    private let getWorkoutsInRangeUseCase: GetWorkoutsInRangeUseCase
    private let clearAllDataUseCase: ClearAllDataUseCase
    private let updateApiKeyUseCase: UpdateApiKeyUseCase
    private let testConnectionUseCase: TestConnectionUseCase
    private let getAvailableModelsUseCase: GetAvailableModelsUseCase
    private let checkLocalSupportUseCase: CheckLocalSupportUseCase
    private let exportUserDataUseCase: ExportUserDataUseCase
    private let getVolumeInsightsUseCase: GetVolumeInsightsUseCase
    private let getFrequencyInsightsUseCase: GetFrequencyInsightsUseCase
    private let getMuscleDistributionInsightsUseCase: GetMuscleDistributionInsightsUseCase

    // Stores
    private let canvasStore: CanvasStore
    private let settingsStore: SettingsStore
    private let insightsStore: InsightsStore
    private let ledgerStore: LedgerStore

    init() {
        do {
            let schema = Schema([
                ScribbleEntity.self,
                ExerciseEntity.self,
                SetEntity.self,
                WorkoutEntity.self
            ])

            modelContainer = try ModelContainer(for: schema)

            // Repositories
            let scribbleRepo = ScribbleRepositoryImpl(modelContainer: modelContainer)
            let configRepo = ConfigRepositoryImpl()
            let workoutRepo = WorkoutRepositoryImpl(modelContainer: modelContainer)
            let settingsRepo = SettingsRepositoryImpl(modelContainer: modelContainer)

            self.scribbleRepository = scribbleRepo
            self.configRepository = configRepo
            self.workoutRepository = workoutRepo
            self.settingsRepository = settingsRepo

            // AI Services
            let localLLM = LocalLLMService(configRepository: configRepo)
            let geminiLLM = GeminiLLMService(configRepository: configRepo, settingsRepository: settingsRepo)
            let routingLLM = RoutingLLMService(configRepository: configRepo, localService: localLLM, geminiService: geminiLLM)
            self.llmProvider = routingLLM

            // Use Cases
            self.getScribblesForDateUseCase = GetScribblesForDateUseCase(repository: scribbleRepo)
            self.addRawScribbleUseCase = AddRawScribbleUseCase(repository: scribbleRepo)
            self.confirmScribbleUseCase = ConfirmScribbleUseCase(scribbleRepository: scribbleRepo, workoutRepository: workoutRepo)
            self.deleteScribbleUseCase = DeleteScribbleUseCase(repository: scribbleRepo)
            self.parsePendingScribblesUseCase = ParsePendingScribblesUseCase(scribbleRepository: scribbleRepo, llmProvider: routingLLM)
            self.getAIOverviewUseCase = GetAIOverviewUseCase(workoutRepository: workoutRepo, llmProvider: routingLLM)
            self.getWorkoutsByDateUseCase = GetWorkoutsByDateUseCase(repository: workoutRepo)
            self.getWorkoutsInRangeUseCase = GetWorkoutsInRangeUseCase(repository: workoutRepo)

            self.clearAllDataUseCase = ClearAllDataUseCase(repository: settingsRepo)
            self.updateApiKeyUseCase = UpdateApiKeyUseCase(repository: settingsRepo)
            self.testConnectionUseCase = TestConnectionUseCase(llmService: routingLLM)
            self.getAvailableModelsUseCase = GetAvailableModelsUseCase(llmService: routingLLM)
            self.checkLocalSupportUseCase = CheckLocalSupportUseCase(localLLM: localLLM)
            self.exportUserDataUseCase = ExportUserDataUseCase(repository: settingsRepo)
            self.getVolumeInsightsUseCase = GetVolumeInsightsUseCase(workoutRepository: workoutRepo)
            self.getFrequencyInsightsUseCase = GetFrequencyInsightsUseCase(workoutRepository: workoutRepo)
            self.getMuscleDistributionInsightsUseCase = GetMuscleDistributionInsightsUseCase(workoutRepository: workoutRepo)

            // Stores
            self.canvasStore = CanvasStore(
                getScribblesForDateUseCase: getScribblesForDateUseCase,
                addRawScribbleUseCase: addRawScribbleUseCase,
                confirmScribbleUseCase: confirmScribbleUseCase,
                deleteScribbleUseCase: deleteScribbleUseCase,
                parsePendingScribblesUseCase: parsePendingScribblesUseCase,
                getAIOverviewUseCase: getAIOverviewUseCase,
                configRepository: configRepo
            )

            self.settingsStore = SettingsStore(
                configRepository: configRepo,
                settingsRepository: settingsRepo,
                updateApiKeyUseCase: updateApiKeyUseCase,
                testConnectionUseCase: testConnectionUseCase,
                getAvailableModelsUseCase: getAvailableModelsUseCase,
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
                getWorkoutsInRangeUseCase: getWorkoutsInRangeUseCase
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
                ledgerStore: ledgerStore
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

    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            CanvasView(
                store: canvasStore,
                settingsStore: settingsStore
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
                onNavigateToWorkoutDetails: { _ in /* TODO: Implement navigation */ }
            )
            .tabItem {
                Label(String(localized: "Ledger"), systemImage: "person.fill")
            }
            .tag(2)
        }
        .tint(.scribblePrimary)
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
