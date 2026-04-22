import SwiftUI
import CoreDesignSystem
import CoreModel
import CoreCommon
import FeatureExercises
import FeatureWorkouts

public struct LedgerView: View {
    @Bindable var store: LedgerStore
    let onNavigateToCanvas: () -> Void
    
    // Dependencies for sub-screens
    let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    let configRepository: ConfigRepository
    let workoutRepository: WorkoutRepository

    @State private var showingDatePicker = false
    @State private var tempStartDate: Date = .now
    @State private var tempEndDate: Date = .now

    public init(
        store: LedgerStore,
        onNavigateToCanvas: @escaping () -> Void,
        getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
        getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
        configRepository: ConfigRepository,
        workoutRepository: WorkoutRepository
    ) {
        self.store = store
        self.onNavigateToCanvas = onNavigateToCanvas
        self.getExerciseDetailsUseCase = getExerciseDetailsUseCase
        self.getExerciseAIInsightUseCase = getExerciseAIInsightUseCase
        self.configRepository = configRepository
        self.workoutRepository = workoutRepository
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()

                VStack(spacing: 0) {
                    LedgerHeader(
                        dateRange: store.state.dateRangeString,
                        onDateRangeTapped: {
                            tempStartDate = store.state.startDate
                            tempEndDate = store.state.endDate
                            showingDatePicker = true
                        }
                    )

                    ScrollView {
                        ledgerContent
                    }
                    .padding(.vertical)
                }
            }
            .navigationTitle(String(localized: "History"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar { toolbarContent }
        }
        .sheet(isPresented: $showingDatePicker) {
            dateRangePickerSheet
        }
        .sheet(item: Binding(
            get: { store.state.navigationState },
            set: { if $0 == nil { store.handleIntent(.dismissDetails) } }
        )) { navState in
            switch navState {
            case .exerciseDetails(let name):
                ExerciseDetailsView(
                    store: ExerciseDetailsStore(
                        exerciseName: name,
                        getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                        getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                        configRepository: configRepository
                    ),
                    onDismiss: { store.handleIntent(.dismissDetails) }
                )
            case .workoutExercises(let id):
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
                        store.handleIntent(.dismissDetails)
                        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
                            store.handleIntent(.exerciseTapped(name: name))
                        }
                    },
                    onDismiss: { store.handleIntent(.dismissDetails) }
                )
            }
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .topBarTrailing) {
            Button(action: { store.handleIntent(.refresh) }) {
                Image(systemName: "arrow.clockwise")
                    .foregroundStyle(Color.scribblePrimary)
            }
        }
    }

    private var ledgerContent: some View {
        Group {
            if store.state.isLoading {
                loadingView
            } else if store.state.groupedWorkouts.isEmpty {
                emptyView
            } else {
                workoutsList
            }
        }
    }

    private var loadingView: some View {
        ProgressView()
            .tint(Color.scribblePrimary)
            .padding(.top, 40)
    }

    private var emptyView: some View {
        EmptyLedgerContent(
            message: String(localized: "Could not find any workouts for this date range"),
            ctaLabel: String(localized: "Start scribbling"),
            onCTATapped: onNavigateToCanvas
        )
    }

    private var workoutsList: some View {
        LazyVStack(alignment: .leading, spacing: 16) {
            ForEach(store.state.groupedWorkouts) { group in
                WorkoutItem(
                    dateString: group.dateString,
                    workouts: group.workouts,
                    weightUnit: store.state.weightUnit,
                    onWorkoutTapped: { workoutId in
                        store.handleIntent(.workoutTapped(id: workoutId))
                    },
                    onExerciseTapped: { name in
                        store.handleIntent(.exerciseTapped(name: name))
                    }
                )
                .padding(.horizontal)
            }
        }
    }

    private var dateRangePickerSheet: some View {
        NavigationStack {
            Form {
                DatePicker(String(localized: "Start Date"), selection: $tempStartDate, displayedComponents: .date)
                DatePicker(String(localized: "End Date"), selection: $tempEndDate, in: tempStartDate..., displayedComponents: .date)
            }
            .navigationTitle(String(localized: "Select Date Range"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(String(localized: "Cancel")) { showingDatePicker = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(String(localized: "Done")) {
                        store.handleIntent(.updateDateRange(startDate: tempStartDate, endDate: tempEndDate))
                        showingDatePicker = false
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}
