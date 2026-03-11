import SwiftUI

public struct CanvasView: View {
    @ObservedObject private var viewModel: CanvasViewModel
    let onSettingsTap: () -> Void

    public init(viewModel: CanvasViewModel, onSettingsTap: @escaping () -> Void = {}) {
        self.viewModel = viewModel
        self.onSettingsTap = onSettingsTap
    }

    // Groups scribbles by calendar day, newest date at top, items within a day ascending
    private var groupedScribbles: [(label: String, items: [Scribble])] {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())

        let grouped = Dictionary(grouping: viewModel.uiState.scribbles) { item in
            calendar.startOfDay(for: item.createdAt)
        }

        return grouped
            .sorted { $0.key > $1.key }
            .map { (date, items) in
                let label = dateLabel(date, today: today, calendar: calendar)
                let sorted = items.sorted { $0.createdAt < $1.createdAt }
                return (label: label, items: sorted)
            }
    }

    private func dateLabel(_ date: Date, today: Date, calendar: Calendar) -> String {
        let formatter = DateFormatter()
        formatter.dateFormat = "MMMM d"
        let dayStr = formatter.string(from: date)
        if calendar.isDate(date, inSameDayAs: today) {
            return "Today, \(dayStr)"
        } else if let yesterday = calendar.date(byAdding: .day, value: -1, to: today),
                  calendar.isDate(date, inSameDayAs: yesterday) {
            return "Yesterday, \(dayStr)"
        } else {
            return dayStr
        }
    }

    public var body: some View {
        VStack(spacing: 0) {
            topNavBar

            if viewModel.uiState.scribbles.isEmpty {
                EmptyFeedView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 0) {
                        ForEach(groupedScribbles, id: \.label) { group in
                            Text(group.label)
                                .font(.system(size: 12, weight: .regular))
                                .foregroundStyle(ScribbleFitColor.midGray)
                                .padding(.horizontal, ScribbleFitSpacing.medium)
                                .padding(.top, ScribbleFitSpacing.large)
                                .padding(.bottom, ScribbleFitSpacing.small)

                            ForEach(group.items) { item in
                                scribbleRow(item)
                                    .padding(.horizontal, ScribbleFitSpacing.medium)
                                    .padding(.bottom, ScribbleFitSpacing.medium)
                            }
                        }
                    }
                }
                .frame(maxWidth: .infinity, maxHeight: .infinity)
            }

            ScribbleInputBar(
                text: Binding(
                    get: { viewModel.uiState.scribbleText },
                    set: { viewModel.onTextChange($0) }
                ),
                isSyncing: viewModel.uiState.isSyncing,
                onSubmit: viewModel.submitScribble
            )
            .padding(.horizontal, ScribbleFitSpacing.medium)
            .padding(.top, ScribbleFitSpacing.small)
            .padding(.bottom, ScribbleFitSpacing.medium)
        }
        .background(ScribbleFitColor.background)
    }

    private var topNavBar: some View {
        HStack {
            Text("ScribbleFit")
                .font(.system(size: 28, weight: .semibold))
                .foregroundStyle(ScribbleFitColor.richBlack)
            Spacer()
            Button(action: onSettingsTap) {
                Image(systemName: "gearshape")
                    .font(.system(size: 20, weight: .regular))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                    .frame(width: 36, height: 36)
            }
            .buttonStyle(.plain)
        }
        .padding(.horizontal, ScribbleFitSpacing.medium)
        .padding(.top, 16)
        .padding(.bottom, 12)
    }

    @ViewBuilder
    private func scribbleRow(_ item: Scribble) -> some View {
        switch item {
        case .raw(let id, _, let rawText, let status):
            ScribbleCard(
                id: id,
                rawText: rawText,
                status: status,
                onRetry: viewModel.onRetryScribble
            )
        case .parsed(_, _, let exercise):
            ParsedScribbleCard(
                exercise: exercise,
                onConfirm: { workout in viewModel.onConfirmClick(parsedWorkout: workout, scribbleId: item.id) }
            )
        case .insight(_, _, let displayText, _):
            PromptCard(text: displayText)
        }
    }
}
