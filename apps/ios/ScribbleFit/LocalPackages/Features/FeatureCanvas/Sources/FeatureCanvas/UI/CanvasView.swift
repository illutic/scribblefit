import SwiftUI
import CoreModel
import CoreDesignSystem
import CoreCommon
import FeatureSettings
import FeatureExercises
import FeatureScribble

public struct CanvasView: View {
    @Bindable var store: CanvasStore
    let settingsStore: SettingsStore
    
    // Dependencies for sub-screens
    let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    let getExerciseTrendsUseCase: GetExerciseTrendDataUseCase
    let getExerciseHistoryUseCase: GetExerciseHistoryUseCase
    let configRepository: ConfigRepository
    let exerciseRepository: ExerciseRepository
    let manualEditScribbleUseCase: FeatureScribble.ManualEditScribbleUseCase
    let createManualScribbleUseCase: FeatureScribble.CreateManualScribbleUseCase
    let addManualExerciseUseCase: AddManualExerciseUseCase
    let addSetToExerciseUseCase: AddSetToExerciseUseCase
    let scribbleRepository: ScribbleRepository
    let confirmScribbleUseCase: ConfirmScribbleUseCase

    @State private var selectedExerciseName: String? = nil
    @State private var selectedScribbleId: UUID? = nil
    @State private var scribbleToDelete: UUID? = nil
    
    public init(
        store: CanvasStore,
        settingsStore: SettingsStore,
        getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
        getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
        getExerciseTrendsUseCase: GetExerciseTrendDataUseCase,
        getExerciseHistoryUseCase: GetExerciseHistoryUseCase,
        configRepository: ConfigRepository,
        exerciseRepository: ExerciseRepository,
        manualEditScribbleUseCase: FeatureScribble.ManualEditScribbleUseCase,
        createManualScribbleUseCase: FeatureScribble.CreateManualScribbleUseCase,
        addManualExerciseUseCase: AddManualExerciseUseCase,
        addSetToExerciseUseCase: AddSetToExerciseUseCase,
        scribbleRepository: ScribbleRepository,
        confirmScribbleUseCase: ConfirmScribbleUseCase
    ) {
        self.store = store
        self.settingsStore = settingsStore
        self.getExerciseDetailsUseCase = getExerciseDetailsUseCase
        self.getExerciseAIInsightUseCase = getExerciseAIInsightUseCase
        self.getExerciseTrendsUseCase = getExerciseTrendsUseCase
        self.getExerciseHistoryUseCase = getExerciseHistoryUseCase
        self.configRepository = configRepository
        self.exerciseRepository = exerciseRepository
        self.manualEditScribbleUseCase = manualEditScribbleUseCase
        self.createManualScribbleUseCase = createManualScribbleUseCase
        self.addManualExerciseUseCase = addManualExerciseUseCase
        self.addSetToExerciseUseCase = addSetToExerciseUseCase
        self.scribbleRepository = scribbleRepository
        self.confirmScribbleUseCase = confirmScribbleUseCase
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
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
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
                    exerciseDetailsSheet(for: name, onDismiss: { store.onIntent(.dismissDetails) })
                case .scribbleDetails(let id):
                    scribbleDetailsSheet(for: id)
                }
            }
            .sheet(isPresented: $store.state.isSettingsVisible) {
                settingsView()
            }
            .sheet(isPresented: $store.state.isAddExerciseSheetVisible) {
                AddExerciseSheetView(
                    weightUnitLabel: store.state.weightUnitLabel,
                    onDismiss: { store.onIntent(.hideAddExerciseSheet) },
                    onSave: { name, muscle, sets in
                        store.onIntent(.saveManualExercise(name: name, muscleGroup: muscle, sets: sets))
                    }
                )
            }
        }
    }
    
    // MARK: - Components
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        #if os(iOS)
        ToolbarItem(placement: .topBarTrailing) {
            Button(action: { store.onIntent(.navigateToSettings) }) {
                Image(systemName: "gearshape.fill")
                    .font(.system(size: 18))
                    .foregroundStyle(Color.scribblePrimary)
            }
        }
        #else
        ToolbarItem {
            Button(action: { store.onIntent(.navigateToSettings) }) {
                Image(systemName: "gearshape.fill")
            }
        }
        #endif
        
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
            onScribbleDetailsClick: { id in store.onIntent(.navigateToScribbleDetails(id)) },
            onIntent: store.onIntent
        )
    }
    
    private var footerView: some View {
        CanvasFooter(
            text: $store.state.currentScribbleText,
            isSending: store.state.isLoading,
            onSend: { store.onIntent(.addScribble(store.state.currentScribbleText)) },
            onManualAdd: { store.onIntent(.showAddExerciseSheet) }
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
            },
            onDeleteExercise: { exId in
                store.onIntent(.deleteExercise(exId))
            },
            onAddSet: { exId in
                store.onIntent(.addSet(exId))
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
    private func exerciseDetailsSheet(for name: String, onDismiss onDismiss: @escaping () -> Void) -> some View {
        ExerciseDetailsView(
            store: ExerciseDetailsStore(
                exerciseName: name,
                getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                removeExerciseUseCase: RemoveExerciseUseCase(exerciseRepository: exerciseRepository, scribbleRepository: scribbleRepository),
                configRepository: configRepository
            ),
            onDismiss: onDismiss,
            getExerciseTrendDataUseCase: getExerciseTrendsUseCase,
            getExerciseHistoryUseCase: getExerciseHistoryUseCase,
            configRepository: configRepository
        )
    }
    
    @ViewBuilder
    private func scribbleDetailsSheet(for id: UUID) -> some View {
        ScribbleDetailsView(
            store: ScribbleDetailsStore(
                scribbleId: id,
                scribbleRepository: scribbleRepository,
                configRepository: configRepository,
                confirmScribbleUseCase: confirmScribbleUseCase
            ),
            getExerciseDetailsUseCase: getExerciseDetailsUseCase,
            getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
            getExerciseTrendsUseCase: getExerciseTrendsUseCase,
            getExerciseHistoryUseCase: getExerciseHistoryUseCase,
            configRepository: configRepository,
            exerciseRepository: exerciseRepository,
            scribbleRepository: scribbleRepository
        )
    }
}
