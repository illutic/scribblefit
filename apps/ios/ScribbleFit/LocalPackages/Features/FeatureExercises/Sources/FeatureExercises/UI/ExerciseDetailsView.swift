import SwiftUI
import CoreModel
import CoreDesignSystem

public struct ExerciseDetailsView: View {
    @Bindable var store: ExerciseDetailsStore
    let onDismiss: () -> Void
    
    public init(store: ExerciseDetailsStore, onDismiss: @escaping () -> Void) {
        self.store = store
        self.onDismiss = onDismiss
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
                                    onViewAllClick: { /* TODO */ }
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
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
            }
        }
    }
}
