import SwiftUI

public struct MainView: View {
    private let canvasRepository: CanvasRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    
    public init(canvasRepository: CanvasRepository, processScribbleUseCase: ProcessScribbleUseCase) {
        self.canvasRepository = canvasRepository
        self.processScribbleUseCase = processScribbleUseCase
    }
    
    public var body: some View {
        TabView {
            CanvasView(viewModel: CanvasViewModel(
                canvasRepository: canvasRepository,
                processScribbleUseCase: processScribbleUseCase
            ))
            .tabItem {
                Label("Workout", systemImage: "dumbbell.fill")
            }
            
            AnalyticsView()
                .tabItem { Label("Analytics", systemImage: "chart.bar.fill") }
            
            ExercisesView()
                .tabItem { Label("Exercises", systemImage: "list.bullet") }
            
            ProfileView()
                .tabItem { Label("Profile", systemImage: "person.fill") }
        }
        .accentColor(ScribbleFitColor.primaryText)
    }
}
