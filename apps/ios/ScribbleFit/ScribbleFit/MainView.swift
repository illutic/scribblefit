import SwiftUI

public struct MainView: View {
    @StateObject private var navManager = NavigationManager()
    
    private let canvasRepository: CanvasRepository
    private let analysisRepository: AnalysisRepository
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    
    public init(
        canvasRepository: CanvasRepository,
        analysisRepository: AnalysisRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.analysisRepository = analysisRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase
    }
    
    public var body: some View {
        TabView(selection: $navManager.activeTab) {
            // Workout Tab
            NavigationStack(path: $navManager.workoutPath) {
                CanvasView(viewModel: CanvasViewModel(
                    canvasRepository: canvasRepository,
                    analysisRepository: analysisRepository,
                    processScribbleUseCase: processScribbleUseCase,
                    executeQuickActionUseCase: executeQuickActionUseCase
                ))
                .navigationTitle(AppTab.workout.title)
                .navigationBarTitleDisplayMode(.inline)
            }
            .tabItem {
                Label(AppTab.workout.title, systemImage: AppTab.workout.icon)
            }
            .tag(AppTab.workout)
            
            // Analytics Tab
            NavigationStack(path: $navManager.analyticsPath) {
                AnalyticsView()
                    .navigationTitle(AppTab.analytics.title)
                    .navigationBarTitleDisplayMode(.inline)
            }
            .tabItem {
                Label(AppTab.analytics.title, systemImage: AppTab.analytics.icon)
            }
            .tag(AppTab.analytics)
            
            // Exercises Tab
            NavigationStack(path: $navManager.exercisesPath) {
                ExercisesView()
                    .navigationTitle(AppTab.exercises.title)
                    .navigationBarTitleDisplayMode(.inline)
            }
            .tabItem {
                Label(AppTab.exercises.title, systemImage: AppTab.exercises.icon)
            }
            .tag(AppTab.exercises)
            
            // Profile Tab
            NavigationStack(path: $navManager.profilePath) {
                ProfileView()
                    .navigationTitle(AppTab.profile.title)
                    .navigationBarTitleDisplayMode(.inline)
            }
            .tabItem {
                Label(AppTab.profile.title, systemImage: AppTab.profile.icon)
            }
            .tag(AppTab.profile)
        }
        .accentColor(ScribbleFitColor.primaryText)
        .environmentObject(navManager)
    }
}
