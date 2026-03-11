import SwiftUI

// MARK: - Parsed Scribble Card (Confirmation)

struct ParsedScribbleCard: View {
    let exercise: SyncExercise
    let onConfirm: (ParsedWorkout) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack(alignment: .top) {
                Text(exercise.canonicalName)
                    .font(.system(size: 17, weight: .semibold))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                Spacer()
                Button(action: {
                    let parsedSets = exercise.sets.map { s in
                        ParsedSet(weight: s.weight, reps: s.reps, rpe: s.rpe, notes: s.notes)
                    }
                    let parsedExercise = ParsedExercise(
                        canonicalName: exercise.canonicalName,
                        muscleGroup: exercise.muscleGroup,
                        sets: parsedSets
                    )
                    let formatter = DateFormatter()
                    formatter.dateFormat = "yyyy-MM-dd"
                    let workout = ParsedWorkout(date: formatter.string(from: Date()), exercises: [parsedExercise])
                    onConfirm(workout)
                }) {
                    Text("✓ Log")
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

    private func setSummary(for sets: [SyncExerciseSet]) -> String {
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
    let id: String
    let rawText: String
    let status: ScribbleSyncStatus
    let onRetry: (String) -> Void

    var body: some View {
        HStack(spacing: 12) {
            Text(rawText)
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
            if case .failed = status { onRetry(id) }
        }
    }

    @ViewBuilder
    private var statusDot: some View {
        switch status {
        case .pending:
            Circle()
                .fill(ScribbleFitColor.midGray)
                .frame(width: 8, height: 8)
        case .failed:
            Circle()
                .fill(ScribbleFitColor.dangerRed)
                .frame(width: 8, height: 8)
        case .logged, .completed:
            EmptyView()
        }
    }
}

// MARK: - Prompt / Insight Card

struct PromptCard: View {
    let text: String

    var body: some View {
        HStack(alignment: .top, spacing: 8) {
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
