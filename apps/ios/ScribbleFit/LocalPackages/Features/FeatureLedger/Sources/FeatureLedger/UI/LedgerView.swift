import SwiftUI
import CoreDesignSystem
import CoreModel

public struct LedgerView: View {
    @Bindable var store: LedgerStore
    let onNavigateToCanvas: () -> Void
    let onNavigateToWorkoutDetails: (UUID) -> Void

    @State private var showingDatePicker = false
    @State private var tempStartDate: Date = .now
    @State private var tempEndDate: Date = .now

    public init(
        store: LedgerStore,
        onNavigateToCanvas: @escaping () -> Void,
        onNavigateToWorkoutDetails: @escaping (UUID) -> Void
    ) {
        self.store = store
        self.onNavigateToCanvas = onNavigateToCanvas
        self.onNavigateToWorkoutDetails = onNavigateToWorkoutDetails
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()

                ScrollView {
                    VStack(alignment: .leading, spacing: 20) {
                        LedgerHeader(
                            dateRange: store.state.dateRangeString,
                            onDateRangeTapped: {
                                tempStartDate = store.state.startDate
                                tempEndDate = store.state.endDate
                                showingDatePicker = true
                            }
                        )

                        if store.state.isLoading {
                            LedgerLoadingView()
                        } else if store.state.isEmpty {
                            EmptyLedgerContent(
                                message: String(localized: "Your history is empty"),
                                ctaLabel: String(localized: "Start your first session on the Canvas"),
                                onCTATapped: onNavigateToCanvas
                            )
                        } else {
                            LazyVStack(alignment: .leading, spacing: 16) {
                                ForEach(store.state.groupedWorkouts) { group in
                                    WorkoutItem(
                                        dateString: group.dateString,
                                        exercises: group.exercises,
                                        onTapped: {
                                            // TODO: Navigate to day details or first workout
                                        }
                                    )
                                    .padding(.horizontal)
                                }
                            }
                        }
                    }
                    .padding(.vertical)
                }
            }
            .navigationTitle(String(localized: "Ledger"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Text(String(localized: "ScribbleFit"))
                        .font(.scribbleHeadlineSmall)
                        .foregroundStyle(Color.scribblePrimary)
                }
            }
            .sheet(isPresented: $showingDatePicker) {
                dateRangePickerSheet
            }
            .onAppear {
                store.handleIntent(.refresh)
            }
        }
    }

    private var dateRangePickerSheet: some View {
        NavigationStack {
            Form {
                DatePicker(String(localized: "Start Date"), selection: $tempStartDate, displayedComponents: .date)
                DatePicker(String(localized: "End Date"), selection: $tempEndDate, in: tempStartDate..., displayedComponents: .date)
            }
            .navigationTitle(String(localized: "Select Range"))
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button(String(localized: "Cancel")) {
                        showingDatePicker = false
                    }
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
