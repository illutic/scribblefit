import SwiftUI

public struct MainView: View {
    @StateObject private var navManager = NavigationManager()
    
    private let canvasRepository: CanvasRepository
    private let analysisRepository: AnalysisRepository
    private let userRepository: UserRepository
    private let settingsRepository: SettingsRepository
    private let modelRepository: ModelRepository
    
    private let processScribbleUseCase: ProcessScribbleUseCase
    private let executeQuickActionUseCase: ExecuteQuickActionUseCase
    private let confirmWorkoutUseCase: ConfirmWorkoutUseCase
    private let listenForSyncItemsUseCase: ListenForSyncItemsUseCase

    public init(
        canvasRepository: CanvasRepository,
        analysisRepository: AnalysisRepository,
        userRepository: UserRepository,
        settingsRepository: SettingsRepository,
        modelRepository: ModelRepository,
        processScribbleUseCase: ProcessScribbleUseCase,
        executeQuickActionUseCase: ExecuteQuickActionUseCase,
        confirmWorkoutUseCase: ConfirmWorkoutUseCase,
        listenForSyncItemsUseCase: ListenForSyncItemsUseCase
    ) {
        self.canvasRepository = canvasRepository
        self.analysisRepository = analysisRepository
        self.userRepository = userRepository
        self.settingsRepository = settingsRepository
        self.modelRepository = modelRepository
        self.processScribbleUseCase = processScribbleUseCase
        self.executeQuickActionUseCase = executeQuickActionUseCase
        self.confirmWorkoutUseCase = confirmWorkoutUseCase
        self.listenForSyncItemsUseCase = listenForSyncItemsUseCase
    }
    
    public var body: some View {
        TabView(selection: $navManager.activeTab) {
            // Workout Tab
            NavigationStack(path: $navManager.workoutPath) {
                CanvasView(viewModel: CanvasViewModel(
                    canvasRepository: canvasRepository,
                    analysisRepository: analysisRepository,
                    processScribbleUseCase: processScribbleUseCase,
                    executeQuickActionUseCase: executeQuickActionUseCase,
                    confirmWorkoutUseCase: confirmWorkoutUseCase,
                    listenForSyncItemsUseCase: listenForSyncItemsUseCase
                ))
                .navigationTitle(AppTab.workout.title)
                .navigationBarTitleDisplayMode(.inline)
                .navigationDestination(for: AppDestination.self) { destination in
                    switch destination {
                    case .settings:
                        SettingsView(viewModel: SettingsViewModel(
                            settingsRepository: settingsRepository,
                            modelRepository: modelRepository,
                            navManager: navManager
                        ))
                    }
                }
            }
            .tabItem {
                Label(AppTab.workout.title, systemImage: AppTab.workout.icon)
            }
            .tag(AppTab.workout)
            
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
                ProfileView(viewModel: ProfileViewModel(
                    userRepository: userRepository,
                    navManager: navManager
                ))
                .navigationTitle(AppTab.profile.title)
                .navigationBarTitleDisplayMode(.inline)
                .navigationDestination(for: AppDestination.self) { destination in
                    switch destination {
                    case .settings:
                        SettingsView(viewModel: SettingsViewModel(
                            settingsRepository: settingsRepository,
                            modelRepository: modelRepository,
                            navManager: navManager
                        ))
                    }
                }
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
