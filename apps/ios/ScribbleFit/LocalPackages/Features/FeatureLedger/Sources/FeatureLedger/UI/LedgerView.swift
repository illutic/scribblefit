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
            case .scribbleDetails(let id):
                ScribbleDetailsView(
                    store: ScribbleDetailsStore(
                        scribbleId: id,
                        scribbleRepository: scribbleRepository,
                        configRepository: configRepository,
                        confirmScribbleUseCase: confirmScribbleUseCase
                    )
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
                        store.handleIntent(.scribbleTapped(id: scribbleId))
                    },
                    onExerciseTapped: { name in
                        // We do not have exercise details nav state here anymore, but could add it.
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

extension LedgerState.GroupedScribbles {
    var dateString: String {
        let formatter = DateFormatter()
        formatter.dateStyle = .medium
        formatter.timeStyle = .none
        return formatter.string(from: date)
    }
}
