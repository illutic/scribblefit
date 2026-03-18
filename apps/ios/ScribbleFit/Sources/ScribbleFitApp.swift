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
            let container = try ModelContainer(for: ScribbleEntity.self, ExerciseEntity.self, SetEntity.self)
            self.modelContainer = container
            
            let scribbleRepository = ScribbleRepositoryImpl(modelContainer: container)
            let insightsRepository = InsightsRepositoryImpl(modelContainer: container)
            
            let getScribblesByDateUseCase = GetScribblesByDateUseCase(repository: scribbleRepository)
            let addRawScribbleUseCase = AddRawScribbleUseCase(repository: scribbleRepository)
            
            let getVolumeInsights = GetVolumeInsightsUseCase(repository: insightsRepository)
            let getFrequencyInsights = GetFrequencyInsightsUseCase(repository: insightsRepository)
            let getMuscleDistributionInsights = GetMuscleDistributionInsightsUseCase(repository: insightsRepository)
            
            self.canvasStore = CanvasStore(
                getScribblesByDateUseCase: getScribblesByDateUseCase,
                addRawScribbleUseCase: addRawScribbleUseCase
            )
            
            self.insightsStore = InsightsStore(
                getVolumeInsights: getVolumeInsights,
                getFrequencyInsights: getFrequencyInsights,
                getMuscleDistributionInsights: getMuscleDistributionInsights
            )
        } catch {
            fatalError("Could not initialize SwiftData: \(error.localizedDescription)")
        }
    }

    var body: some Scene {
        WindowGroup {
            ScribbleFitThemeProvider {
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
            }
        }
        .modelContainer(modelContainer)
    }
}
