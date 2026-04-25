import SwiftUI
import CoreModel
import CoreDesignSystem

public struct InsightsView: View {
    @Bindable var store: InsightsStore

    public init(store: InsightsStore) {
        self.store = store
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()

                ScrollView {
                    VStack(spacing: ScribbleFitSpacing.large) {
                        Picker(String(localized: "Period"), selection: Binding(
                            get: { store.state.selectedPeriod },
                            set: { store.onIntent(.selectPeriod($0)) }
                        )) {
                            ForEach(InsightsPeriod.allCases, id: \.self) { period in
                                Text(period.label).tag(period)
                            }
                        }
                        .pickerStyle(.segmented)
                        .accessibilityIdentifier("periodPicker")

                        if store.state.isLoading {
                            InsightsLoadingView(state: store.state)
                        } else if store.state.isEmpty {
                            InsightsEmptyView(state: store.state)
                        } else {
                            InsightsDataView(state: store.state)
                        }
                    }
                    .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    .padding(.vertical, ScribbleFitSpacing.large)
                }
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(store.state.titleText)
                        .font(.scribbleTitleMedium)
                        .fontWeight(.semibold)
                        .foregroundStyle(Color.scribblePrimary)
                }
            }
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .refreshable {
                store.onIntent(.refresh)
            }
        }
    }
}
