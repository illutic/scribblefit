import SwiftUI
import CoreModel
import CoreDesignSystem
import CoreCommon
import FeatureSettings
import FeatureExercises
import FeatureWorkouts

public struct CanvasView: View {
    @Bindable var store: CanvasStore
    let settingsStore: SettingsStore
    
    // Dependencies for sub-screens
    let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    let configRepository: ConfigRepository
    let workoutRepository: WorkoutRepository

    @State private var selectedExerciseName: String? = nil
    @State private var selectedWorkoutId: UUID? = nil
    @State private var scribbleToDelete: UUID? = nil
    
    public init(
        store: CanvasStore,
        settingsStore: SettingsStore,
        getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
        getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
        configRepository: ConfigRepository,
        workoutRepository: WorkoutRepository
    ) {
        self.store = store
        self.settingsStore = settingsStore
        self.getExerciseDetailsUseCase = getExerciseDetailsUseCase
        self.getExerciseAIInsightUseCase = getExerciseAIInsightUseCase
        self.configRepository = configRepository
        self.workoutRepository = workoutRepository
    }

    public var body: some View {
        NavigationStack {
            ZStack(alignment: .bottom) {
                Color.scribbleBackground.ignoresSafeArea()

                bodyContent
                    .safeAreaInset(edge: .bottom) {
                        Color.clear.frame(height: 100) // Space for floating footer
                    }

                footerView
                    .padding(.bottom, 8)
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar { toolbarContent }
            .sheet(item: $store.state.selectedScribble) { scribble in
                scribbleConfirmationSheet(for: scribble)
            }
            .alert(String(localized: "Delete Scribble?"), isPresented: Binding(
                get: { scribbleToDelete != nil },
                set: { if !$0 { scribbleToDelete = nil } }
            )) {
                deleteAlertActions
            } message: {
                Text(String(localized: "Are you sure you want to delete this scribble? This action cannot be undone."))
            }
            .sheet(isPresented: $store.state.isDatePickerVisible) {
                datePickerSheet
            }
            .sheet(item: Binding(
                get: { store.state.navigationState },
                set: { if $0 == nil { store.onIntent(.dismissDetails) } }
            )) { navState in
                switch navState {
                case .exerciseDetails(let name):
                    exerciseDetailsSheet(for: name)
                case .workoutExercises(let id):
                    workoutExercisesSheet(for: id)
                }
            }
            .sheet(isPresented: $store.state.isSettingsVisible) {
                settingsView()
            }
        }
    }
    
    // MARK: - Components
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button(action: { store.onIntent(.navigateToSettings) }) {
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 18))
                    .foregroundStyle(Color.scribblePrimary)
            }
        }
        
        ToolbarItem(placement: .principal) {
            HStack(spacing: 12) {
                Button(action: { store.onIntent(.onPreviousDayClick) }) {
                    Image(systemName: "chevron.left")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                }
                
                Button(action: { store.onIntent(.showDatePicker) }) {
                    Text(store.state.dateString)
                        .font(.scribbleTitleMedium)
                        .foregroundStyle(Color.scribblePrimary)
                }
                
                Button(action: { store.onIntent(.onNextDayClick) }) {
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                }
            }
        }
    }
    
    private var bodyContent: some View {
        CanvasBodyView(
            scribbles: store.state.scribbles,
            aiInsights: store.state.aiInsights,
            isGeneratingInsights: store.state.isGeneratingInsights,
            weightUnit: store.state.weightUnit,
            emptyText: store.state.emptyScribbleText,
            onScribbleClick: { store.onIntent(.clickOnScribble($0)) },
            onExerciseClick: { name in store.onIntent(.navigateToExerciseDetails(name)) },
            onWorkoutExercisesClick: { id in store.onIntent(.navigateToWorkoutExercises(id)) },
            onIntent: store.onIntent
        )
    }
    
    private var footerView: some View {
        CanvasFooter(
            text: $store.state.currentScribbleText,
            isSending: store.state.isLoading,
            onSend: { store.onIntent(.addScribble(store.state.currentScribbleText)) }
        )
    }
    
    // MARK: - Navigation & Sheets
    
    @ViewBuilder
    private func settingsView() -> some View {
        SettingsView(
            store: settingsStore,
            onDismiss: { store.onIntent(.dismissSettings) }
        )
    }
    
    @ViewBuilder
    private func scribbleConfirmationSheet(for scribble: Scribble) -> some View {
        ScribbleConfirmationBottomSheet(
            scribble: scribble,
            weightUnit: store.state.weightUnit,
            onConfirm: { updated in
                store.onIntent(.confirmScribble(updated))
            },
            onDelete: { _ in
                scribbleToDelete = scribble.id
            },
            onDismiss: {
                store.onIntent(.dismissScribbleDialog)
            },
            onUpdateExerciseName: { exId, name in
                store.onIntent(.updateExerciseName(exId, name))
            },
            onUpdateSetWeight: { exId, setId, weight in
                store.onIntent(.updateSetWeight(exId, setId, weight))
            },
            onUpdateSetReps: { exId, setId, reps in
                store.onIntent(.updateSetReps(exId, setId, reps))
            },
            onDeleteSet: { exId, setId in
                store.onIntent(.deleteSet(exId, setId))
            }
        )
    }
    
    @ViewBuilder
    private var deleteAlertActions: some View {
        Button(String(localized: "Delete"), role: .destructive) {
            if let id = scribbleToDelete {
                store.onIntent(.deleteScribble(id))
            }
        }
        Button(String(localized: "Cancel"), role: .cancel) {}
    }
    
    @ViewBuilder
    private var datePickerSheet: some View {
        CanvasDatePickerView(
            initialDate: store.state.currentDate,
            onDateSelected: { date in
                store.onIntent(.onDateSelected(date))
            },
            onDismiss: { store.onIntent(.dismissDatePicker) }
        )
    }
    
    @ViewBuilder
    private func exerciseDetailsSheet(for name: String) -> some View {
        ExerciseDetailsView(
            store: ExerciseDetailsStore(
                exerciseName: name,
                getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                configRepository: configRepository
            ),
            onDismiss: { selectedExerciseName = nil }
        )
    }
    
    @ViewBuilder
    private func workoutExercisesSheet(for id: UUID) -> some View {
        WorkoutExercisesView(
            store: WorkoutExercisesStore(
                workoutId: id,
                workoutRepository: workoutRepository,
                configRepository: configRepository,
                calculateWorkoutVolumeUseCase: CalculateWorkoutVolumeUseCase(),
                formatWorkoutSummaryUseCase: FormatWorkoutSummaryUseCase(),
                formatExerciseSummaryUseCase: FormatExerciseSummaryUseCase()
            ),
            onExerciseClick: { name in
                selectedWorkoutId = nil
                DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                    selectedExerciseName = name
                }
            },
            onDismiss: { selectedWorkoutId = nil }
        )
    }
}
