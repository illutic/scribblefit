import SwiftUI

public struct CanvasView: View {
    @ObservedObject private var viewModel: CanvasViewModel
    let onSettingsTap: () -> Void

    public init(viewModel: CanvasViewModel, onSettingsTap: @escaping () -> Void = {}) {
        self.viewModel = viewModel
        self.onSettingsTap = onSettingsTap
    }

    // Groups feed by calendar day, newest date at top, items within a day ascending
    private var groupedFeed: [(label: String, items: [FeedItem])] {
        let calendar = Calendar.current
        let today = calendar.startOfDay(for: Date())

        let grouped = Dictionary(grouping: viewModel.uiState.feedItems) { item in
            calendar.startOfDay(for: item.timestamp)
        }

        return grouped
            .sorted { $0.key > $1.key }
            .map { (date, items) in
                let label = dateLabel(date, today: today, calendar: calendar)
                let sorted = items.sorted { $0.timestamp < $1.timestamp }
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

            if viewModel.uiState.feedItems.isEmpty {
                EmptyFeedView()
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                ScrollView {
                    LazyVStack(alignment: .leading, spacing: 0) {
                        ForEach(groupedFeed, id: \.label) { group in
                            Text(group.label)
                                .font(.system(size: 12, weight: .regular))
                                .foregroundStyle(ScribbleFitColor.midGray)
                                .padding(.horizontal, ScribbleFitSpacing.medium)
                                .padding(.top, ScribbleFitSpacing.large)
                                .padding(.bottom, ScribbleFitSpacing.small)

                            ForEach(group.items) { item in
                                feedItemRow(item)
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
    private func feedItemRow(_ item: FeedItem) -> some View {
        switch item {
        case .scribble(let s):
            ScribbleCard(item: s, onRetry: viewModel.onRetryScribble)
        case .confirmation(let c):
            ConfirmationCard(confirmation: c, onConfirm: viewModel.onConfirmClick)
        case .prompt(let p):
            PromptCard(emoji: p.emoji, text: p.text)
        case .insight(let i):
            PromptCard(emoji: i.emoji, text: i.text)
        }
    }
}
