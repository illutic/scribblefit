import SwiftUI
import CoreModel
import CoreDesignSystem

public struct WorkoutExercisesView: View {
    @Bindable var store: WorkoutExercisesStore
    let onExerciseClick: (String) -> Void
    let onDismiss: () -> Void

    public init(
        store: WorkoutExercisesStore,
        onExerciseClick: @escaping (String) -> Void,
        onDismiss: @escaping () -> Void
    ) {
        self.store = store
        self.onExerciseClick = onExerciseClick
        self.onDismiss = onDismiss
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 32) {
                        // Header Stat Cards
                        HStack(spacing: 12) {
                            StatCardView(
                                label: String(localized: "EXERCISES"),
                                value: "\(store.state.totalExercises)"
                            )
                            StatCardView(
                                label: String(localized: "SETS"),
                                value: "\(store.state.totalSets)"
                            )
                            StatCardView(
                                label: String(localized: "VOLUME"),
                                value: store.state.totalVolume
                            )
                        }
                        .padding(.horizontal, 24)

                        if store.state.isLoading {
                            ProgressView()
                                .tint(Color.scribblePrimary)
                                .padding(.top, 40)
                        } else {
                            LazyVStack(spacing: 16) {
                                ForEach(store.state.uiModels) { exercise in
                                    GlassCard(onClick: { 
                                        store.onIntent(.exerciseClicked(exercise.name))
                                        onExerciseClick(exercise.name)
                                    }) {
                                        ExerciseHeaderView(
                                            name: exercise.name,
                                            formattedSummary: exercise.formattedSummary,
                                            fontSize: 28,
                                            kerning: -1
                                        )

                                        ExerciseStatsView(
                                            estimated1RM: exercise.estimated1RMValue.map {
                                                "\($0)\(store.state.weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs"))"
                                            },
                                            intensity: exercise.intensityValue.map {
                                                "\($0)%"
                                            },
                                            improvement: exercise.improvementValue.map {
                                                "\($0 >= 0 ? "+" : "")\($0)\(store.state.weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs"))"
                                            }
                                        )
                                    }
                                }
                            }
                            .padding(.horizontal, 24)
                        }

                        Spacer()
                            .frame(height: 40)
                    }
                    .padding(.vertical, 24)
                }
            }
            .navigationTitle(store.state.dateString)
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button(action: {
                        store.onIntent(.navigateBack)
                        onDismiss()
                    }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
            }
        }
    }
}
