import SwiftUI

public struct CanvasView: View {
    @ObservedObject private var viewModel: CanvasViewModel

    public init(viewModel: CanvasViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("\(viewModel.uiState.greeting), \(viewModel.uiState.userName)")
                .font(.system(size: 28, weight: .bold))
                .foregroundStyle(ScribbleFitColor.richBlack)
                .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                .padding(.top, ScribbleFitSpacing.large)
                .padding(.bottom, ScribbleFitSpacing.medium)

            ScrollView(.horizontal, showsIndicators: false) {
                HStack(spacing: ScribbleFitSpacing.small) {
                    ForEach(viewModel.uiState.quickActions, id: \.self) { action in
                        ScribbleFitPill(action.rawValue)
                            .onTapGesture { viewModel.onQuickActionClick(action) }
                    }
                }
                .padding(.horizontal, ScribbleFitSpacing.screenPadding)
            }
            .padding(.bottom, ScribbleFitSpacing.medium)

            ScrollView {
                LazyVStack(alignment: .leading, spacing: ScribbleFitSpacing.small) {
                    ForEach(viewModel.uiState.feedItems) { item in
                        FeedItemView(item: item, onConfirm: viewModel.onConfirmClick)
                    }
                }
                .padding(.horizontal, ScribbleFitSpacing.screenPadding)
            }

            ScribbleInputBar(
                text: Binding(
                    get: { viewModel.uiState.scribbleText },
                    set: { viewModel.onTextChange($0) }
                ),
                onSubmit: viewModel.submitScribble
            )
            .padding(ScribbleFitSpacing.screenPadding)
        }
        .background(ScribbleFitColor.background)
    }
}

private struct FeedItemView: View {
    let item: FeedItem
    let onConfirm: (ConfirmationItem) -> Void

    var body: some View {
        switch item {
        case .scribble(let s):
            HStack { Spacer(); ScribbleFitCard { Text(s.rawText).foregroundStyle(ScribbleFitColor.richBlack) } }
        case .confirmation(let c):
            ScribbleFitCard {
                HStack {
                    Text("Parsed: \(c.workout.exercises.count) exercises")
                        .foregroundStyle(ScribbleFitColor.richBlack)
                    Spacer()
                    ScribbleFitPill("Confirm").onTapGesture { onConfirm(c) }
                }
            }
        case .prompt(let p):
            Text("\(p.emoji) \(p.text)").foregroundStyle(ScribbleFitColor.midGray).font(.subheadline)
        case .insight(let i):
            Text("\(i.emoji) \(i.text)").foregroundStyle(ScribbleFitColor.midGray).font(.subheadline)
        }
    }
}

private struct ScribbleInputBar: View {
    @Binding var text: String
    let onSubmit: () -> Void

    var body: some View {
        HStack(spacing: ScribbleFitSpacing.small) {
            TextField("Log workout... e.g. Bench 135x5x3", text: $text)
                .padding(ScribbleFitSpacing.medium)
                .background(ScribbleFitColor.softGray)
                .clipShape(RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.medium))
                .onSubmit(onSubmit)
            if !text.isEmpty {
                Button(action: onSubmit) {
                    Image(systemName: "arrow.up.circle.fill")
                        .font(.system(size: 28))
                        .foregroundStyle(ScribbleFitColor.richBlack)
                }
            }
        }
    }
}
