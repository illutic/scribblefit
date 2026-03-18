import SwiftUI
import SwiftData

@main
@MainActor
struct ScribbleFitApp: App {
    private let modelContainer: ModelContainer
    private let canvasStore: CanvasStore
    private let insightsStore: InsightsStore

    init() {
        do {
            let container = try ModelContainer(for: ScribbleEntity.self, ExerciseEntity.self, SetEntity.self, ScribbleEntity.self) // Ensure all are here
            self.modelContainer = container
            
            let scribbleRepository = ScribbleRepositoryImpl(modelContainer: container)
            let insightsRepository = InsightsRepositoryImpl(modelContainer: container)
            
            let getScribblesByDateUseCase = GetScribblesByDateUseCase(repository: scribbleRepository)
            let addRawScribbleUseCase = AddRawScribbleUseCase(repository: scribbleRepository)
            
            let getVolumeInsights = GetVolumeInsightsUseCase(repository: insightsRepository)
            let getFrequencyInsights = GetFrequencyInsightsUseCase(repository: insightsRepository)
            let getMuscleDistributionInsights = GetMuscleDistributionInsightsUseCase(repository: insightsRepository)
            let getAIOverview = GetAIOverviewUseCase(repository: insightsRepository)
            
            self.canvasStore = CanvasStore(
                getScribblesByDateUseCase: getScribblesByDateUseCase,
                addRawScribbleUseCase: addRawScribbleUseCase
            )
            
            self.insightsStore = InsightsStore(
                getVolumeInsights: getVolumeInsights,
                getFrequencyInsights: getFrequencyInsights,
                getMuscleDistributionInsights: getMuscleDistributionInsights,
                getAIOverview: getAIOverview
            )
        } catch {
            fatalError("Could not initialize SwiftData: \(error.localizedDescription)")
        }
    }

    var body: some Scene {
        WindowGroup {
            ScribbleFitTheme {
                MainTabView(canvasStore: canvasStore, insightsStore: insightsStore)
            }
        }
        .modelContainer(modelContainer)
    }
}

struct MainTabView: View {
    let canvasStore: CanvasStore
    let insightsStore: InsightsStore
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        TabView {
            CanvasView(store: canvasStore)
                .tabItem {
                    Label("Canvas", systemImage: "pencil.and.outline")
                }
            
            InsightsView(store: insightsStore)
                .tabItem {
                    Label("Insights", systemImage: "chart.bar.fill")
                }
            
            Text("Ledger Coming Soon")
                .tabItem {
                    Label("Ledger", systemImage: "list.bullet.rectangle.portrait")
                }
        }
        .tint(colors.richBlack)
        #if os(iOS)
        .toolbarBackground(colors.background, for: .tabBar)
        .toolbarBackground(.visible, for: .tabBar)
        #endif
    }
}
