import SwiftUI
import CoreDesignSystem
import CoreModel
import CoreCommon
import FeatureExercises
import FeatureCanvas
import FeatureScribble

public struct LedgerView: View {
    @Bindable var store: LedgerStore
    let onNavigateToCanvas: () -> Void
    
    // Dependencies for sub-screens
    let getExerciseDetailsUseCase: GetExerciseDetailsUseCase
    let getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase
    let getExerciseTrendsUseCase: GetExerciseTrendDataUseCase
    let getExerciseHistoryUseCase: GetExerciseHistoryUseCase
    let configRepository: ConfigRepository
    let exerciseRepository: ExerciseRepository
    let addManualExerciseUseCase: AddManualExerciseUseCase
    let addSetToExerciseUseCase: AddSetToExerciseUseCase
    let scribbleRepository: ScribbleRepository
    let confirmScribbleUseCase: ConfirmScribbleUseCase

    @State private var showingDatePicker = false
    @State private var tempStartDate: Date = .now
    @State private var tempEndDate: Date = .now

    public init(
        store: LedgerStore,
        onNavigateToCanvas: @escaping () -> Void,
        getExerciseDetailsUseCase: GetExerciseDetailsUseCase,
        getExerciseAIInsightUseCase: GetExerciseAIInsightUseCase,
        getExerciseTrendsUseCase: GetExerciseTrendDataUseCase,
        getExerciseHistoryUseCase: GetExerciseHistoryUseCase,
        configRepository: ConfigRepository,
        exerciseRepository: ExerciseRepository,
        addManualExerciseUseCase: AddManualExerciseUseCase,
        addSetToExerciseUseCase: AddSetToExerciseUseCase,
        scribbleRepository: ScribbleRepository,
        confirmScribbleUseCase: ConfirmScribbleUseCase
    ) {
        self.store = store
        self.onNavigateToCanvas = onNavigateToCanvas
        self.getExerciseDetailsUseCase = getExerciseDetailsUseCase
        self.getExerciseAIInsightUseCase = getExerciseAIInsightUseCase
        self.getExerciseTrendsUseCase = getExerciseTrendsUseCase
        self.getExerciseHistoryUseCase = getExerciseHistoryUseCase
        self.configRepository = configRepository
        self.exerciseRepository = exerciseRepository
        self.addManualExerciseUseCase = addManualExerciseUseCase
        self.addSetToExerciseUseCase = addSetToExerciseUseCase
        self.scribbleRepository = scribbleRepository
        self.confirmScribbleUseCase = confirmScribbleUseCase
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()

                VStack(spacing: 0) {
                    ScrollView {
                        ledgerContent
                    }
                    .padding(.vertical)
                }
            }
            .navigationTitle(String(localized: "History"))
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar { toolbarContent }
        }
        .sheet(isPresented: $showingDatePicker) {
            dateRangePickerSheet
        }
        .sheet(item: Binding(
            get: { store.state.navigationState },
            set: { if $0 == nil { store.onIntent(.dismissDetails) } }
        )) { navState in
            switch navState {
            case .scribbleDetails(let id):
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
            case .exerciseDetails(let name):
                ExerciseDetailsView(
                    store: ExerciseDetailsStore(
                        exerciseName: name,
                        getExerciseDetailsUseCase: getExerciseDetailsUseCase,
                        getExerciseAIInsightUseCase: getExerciseAIInsightUseCase,
                        removeExerciseUseCase: RemoveExerciseUseCase(exerciseRepository: exerciseRepository, scribbleRepository: scribbleRepository),
                        configRepository: configRepository
                    ),
                    onDismiss: { store.onIntent(.dismissDetails) },
                    getExerciseTrendDataUseCase: getExerciseTrendsUseCase,
                    getExerciseHistoryUseCase: getExerciseHistoryUseCase,
                    configRepository: configRepository
                )
            }
        }
    }
    
    @ToolbarContentBuilder
    private var toolbarContent: some ToolbarContent {
        ToolbarItem(placement: .automatic) {
            Button(action: {
                tempStartDate = store.state.startDate
                tempEndDate = store.state.endDate
                showingDatePicker = true
            }) {
                HStack {
                    Image(systemName: "calendar")
                    Text(store.state.dateRangeString)
                    Spacer()
                }
                .padding()
                .background(in: RoundedRectangle(cornerRadius: 12))
                .foregroundStyle(Color.scribblePrimary)
            }
        }
        #if os(iOS)
        ToolbarItem(placement: .topBarTrailing) {
            Button(action: { store.onIntent(.refresh) }) {
                Image(systemName: "arrow.clockwise")
                    .foregroundStyle(Color.scribblePrimary)
            }
        }
        #else
        ToolbarItem {
            Button(action: { store.onIntent(.refresh) }) {
                Image(systemName: "arrow.clockwise")
            }
        }
        #endif
    }

    private var ledgerContent: some View {
        Group {
            if store.state.isLoading {
                loadingView
            } else if store.state.groupedScribbles.isEmpty {
                emptyView
            } else {
                scribblesList
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

    private var scribblesList: some View {
        LazyVStack(alignment: .leading, spacing: 16) {
            ForEach(store.state.groupedScribbles) { group in
                ScribbleItem(
                    dateString: group.dateString,
                    scribbles: group.scribbles,
                    weightUnit: store.state.weightUnit,
                    onScribbleTapped: { scribbleId in
                        store.onIntent(.scribbleTapped(id: scribbleId))
                    },
                    onExerciseTapped: { name in
                        store.onIntent(.exerciseTapped(name: name))
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
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(String(localized: "Cancel")) { showingDatePicker = false }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(String(localized: "Done")) {
                        store.onIntent(.updateDateRange(startDate: tempStartDate, endDate: tempEndDate))
                        showingDatePicker = false
                    }
                }
            }
        }
        .presentationDetents([.medium])
    }
}

extension LedgerState.GroupedScribbles {
    private static let dateFormatter: DateFormatter = {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter
    }()

    var dateString: String {
        return Self.dateFormatter.string(from: date)
    }
}
