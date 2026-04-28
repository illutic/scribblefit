import SwiftUI
import CoreModel
import CoreDesignSystem

public struct ExerciseDetailsView: View {
    @Bindable var store: ExerciseDetailsStore
    let onDismiss: () -> Void
    
    // Dependencies for sub-screens
    let getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase
    let configRepository: ConfigRepository
    
    public init(
        store: ExerciseDetailsStore,
        onDismiss: @escaping () -> Void,
        getExerciseTrendDataUseCase: GetExerciseTrendDataUseCase,
        configRepository: ConfigRepository
    ) {
        self.store = store
        self.onDismiss = onDismiss
        self.getExerciseTrendDataUseCase = getExerciseTrendDataUseCase
        self.configRepository = configRepository
    }
    
    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 32) {
                        if store.state.isLoading {
                            ProgressView()
                                .tint(Color.scribblePrimary)
                                .padding(.top, 40)
                        } else if let details = store.state.details {
                            VStack(spacing: 32) {
                                ExerciseInsightCard(
                                    insight: store.state.aiInsight,
                                    isGenerating: store.state.isGeneratingAI
                                )
                                
                                WeeklyStatsCard(
                                    stats: details.weeklyStats,
                                    weightUnit: store.state.weightUnit == .kgs ? "kg" : "lbs"
                                )
                                
                                TrendsSection(
                                    trends: details.trends,
                                    weightUnit: store.state.weightUnit == .kgs ? "kg" : "lbs",
                                    onViewAllClick: { store.onIntent(.viewAllTrendsTapped) }
                                )
                                
                                HistorySection(
                                    historyCount: details.history.count,
                                    onViewHistoryClick: { /* TODO */ }
                                )
                            }
                            .padding(.horizontal, 24)
                        }
                        
                        Spacer()
                            .frame(height: 40)
                    }
                    .padding(.vertical, 24)
                }
            }
            .navigationTitle(store.state.exerciseName)
            .navigationDestination(isPresented: $store.state.showTrends) {
                ExerciseTrendsView(
                    store: ExerciseTrendsStore(
                        exerciseName: store.state.exerciseName,
                        getExerciseTrendDataUseCase: getExerciseTrendDataUseCase,
                        configRepository: configRepository
                    )
                )
            }
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            #if os(iOS)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
            }
            #endif
        }
    }
}
