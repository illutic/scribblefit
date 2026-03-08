import SwiftUI

// MARK: - Confirmation Card

struct ConfirmationCard: View {
    let confirmation: ConfirmationItem
    let onConfirm: (ConfirmationItem) -> Void

    var body: some View {
        VStack(spacing: ScribbleFitSpacing.small) {
            ForEach(confirmation.workout.exercises, id: \.canonicalName) { exercise in
                ExerciseConfirmationRow(exercise: exercise, onConfirm: { onConfirm(confirmation) })
            }
        }
    }
}

private struct ExerciseConfirmationRow: View {
    let exercise: ParsedExercise
    let onConfirm: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack(alignment: .top) {
                Text(exercise.canonicalName)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                Spacer()
                Button(action: onConfirm) {
                    Text("✓ Logged")
                        .font(.system(size: 12, weight: .regular))
                        .foregroundStyle(ScribbleFitColor.midGray)
                }
                .buttonStyle(.plain)
            }
            Text(setSummary(for: exercise.sets))
                .font(.system(size: 15, weight: .regular))
                .foregroundStyle(ScribbleFitColor.midGray)
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 14)
        .background(ScribbleFitColor.background)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(ScribbleFitColor.lightGray, lineWidth: 1)
        )
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }

    private func setSummary(for sets: [ParsedSet]) -> String {
        let count = sets.count
        guard count > 0 else { return "\(count) sets" }
        let weight = sets[0].weight
        let reps = sets[0].reps
        let weightStr = weight == weight.rounded() ? "\(Int(weight))" : "\(weight)"
        return "\(count) sets · \(weightStr) lb · \(reps) reps"
    }
}

// MARK: - Scribble Card

struct ScribbleCard: View {
    let item: ScribbleItem
    let onRetry: (String) -> Void

    var body: some View {
        HStack(spacing: 12) {
            Text(item.rawText)
                .font(.system(size: 15, weight: .regular))
                .foregroundStyle(ScribbleFitColor.richBlack)
                .lineLimit(1)
            Spacer()
            statusDot
        }
        .padding(.horizontal, 18)
        .frame(height: 52)
        .background(ScribbleFitColor.softGray)
        .clipShape(Capsule())
        .onTapGesture {
            if item.status == .failed { onRetry(item.id) }
        }
    }

    @ViewBuilder
    private var statusDot: some View {
        switch item.status {
        case .pending, .processing:
            Circle()
                .fill(ScribbleFitColor.midGray)
                .frame(width: 8, height: 8)
        case .failed:
            Circle()
                .fill(ScribbleFitColor.dangerRed)
                .frame(width: 8, height: 8)
        case .completed:
            EmptyView()
        }
    }
}

// MARK: - Prompt / Insight Card

struct PromptCard: View {
    let emoji: String
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
            Text(emoji)
                .font(.system(size: 15))
            Text(text)
                .font(.system(size: 15, weight: .regular).italic())
                .foregroundStyle(ScribbleFitColor.midGray)
                .multilineTextAlignment(.leading)
            Spacer()
        }
        .padding(.vertical, 16)
    }
}

// MARK: - Input Bar

public struct ScribbleInputBar: View {
    @Binding var text: String
    let isSyncing: Bool
    let onSubmit: () -> Void

    public init(text: Binding<String>, isSyncing: Bool = false, onSubmit: @escaping () -> Void) {
        self._text = text
        self.isSyncing = isSyncing
        self.onSubmit = onSubmit
    }

    public var body: some View {
        HStack(spacing: 8) {
            TextField("What did you lift today?", text: $text)
                .font(.system(size: 15))
                .foregroundStyle(ScribbleFitColor.richBlack)
                .tint(ScribbleFitColor.richBlack)
                .onSubmit(onSubmit)
            Spacer()
            ZStack {
                Circle()
                    .fill(ScribbleFitColor.softGray)
                    .frame(width: 32, height: 32)
                if isSyncing {
                    ProgressView()
                        .scaleEffect(0.65)
                        .tint(ScribbleFitColor.midGray)
                } else {
                    Button(action: onSubmit) {
                        Image(systemName: "arrow.up")
                            .font(.system(size: 13, weight: .semibold))
                            .foregroundStyle(text.isEmpty ? ScribbleFitColor.midGray : ScribbleFitColor.richBlack)
                    }
                    .buttonStyle(.plain)
                    .disabled(text.isEmpty)
                }
            }
        }
        .padding(.leading, 18)
        .padding(.trailing, 10)
        .frame(height: 52)
        .background(ScribbleFitColor.softGray)
        .clipShape(Capsule())
    }
}

// MARK: - Empty Feed

public struct EmptyFeedView: View {
    public init() {}

    public var body: some View {
        VStack {
            Spacer()
            HStack(alignment: .top, spacing: 8) {
                Text("🏋️")
                    .font(.system(size: 15))
                Text("Start scribbling. Type your first set below.")
                    .font(.system(size: 15, weight: .regular).italic())
                    .foregroundStyle(ScribbleFitColor.midGray)
                    .multilineTextAlignment(.leading)
                Spacer()
            }
            .padding(.horizontal, ScribbleFitSpacing.medium)
            Spacer()
        }
    }
}
