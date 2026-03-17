import SwiftUI
import SwiftData

@main
struct ScribbleFitApp: App {
    private let modelContainer: ModelContainer
    private let store: CanvasStore

    init() {
        do {
            let container = try ModelContainer(for: ScribbleEntity.self)
            self.modelContainer = container
            
            let repository = ScribbleRepositoryImpl(modelContainer: container)
            let getScribblesByDateUseCase = GetScribblesByDateUseCase(repository: repository)
            let addRawScribbleUseCase = AddRawScribbleUseCase(repository: repository)
            
            self.store = CanvasStore(
                getScribblesByDateUseCase: getScribblesByDateUseCase,
                addRawScribbleUseCase: addRawScribbleUseCase
            )
        } catch {
            fatalError("Could not initialize SwiftData: \(error.localizedDescription)")
        }
    }

    var body: some Scene {
        WindowGroup {
            ScribbleFitThemeProvider {
                CanvasView(store: store)
            }
        }
        .modelContainer(modelContainer)
    }
}
