import SwiftUI

struct CanvasHeader: View {
    let userName: String
    let greeting: String
    
    var body: some View {
        HStack {
            Text("\(greeting), \(userName.uppercased())")
                .font(ScribbleFitFont.labelMedium().bold())
                .kerning(0.8)
                .foregroundColor(ScribbleFitColor.primaryText)
            Spacer()
            Image(systemName: "line.3.horizontal")
                .font(.system(size: 20, weight: .medium))
                .foregroundColor(ScribbleFitColor.primaryText)
        }
    }
}

struct QuickActionPills: View {
    let actions: [QuickActionType]
    let onActionClick: (QuickActionType) -> Void
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: 12) {
                ForEach(actions, id: \.self) { action in
                    let label = switch action {
                    case .repeatLast: "Repeat last workout"
                    case .restDay: "Rest Day"
                    case .run5k: "Log 5k Run"
                    }
                    ScribbleFitPill(label) {
                        onActionClick(action)
                    }
                }
            }
        }
    }
}

struct ScribbleInputArea: View {
    @Binding var text: String
    let isSyncing: Bool
    let onSubmit: () -> Void
    
    var body: some View {
        ScribbleFitTextField(
            text: $text,
            placeholder: "Message ScribbleFit...",
            trailingIcon: AnyView(
                Group {
                    if !text.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty {
                        Button(action: onSubmit) {
                            if isSyncing {
                                ProgressView()
                                    .progressViewStyle(CircularProgressViewStyle(tint: ScribbleFitColor.primaryText))
                            } else {
                                Image(systemName: "arrow.up.circle.fill")
                                    .font(.system(size: 32))
                                    .foregroundColor(ScribbleFitColor.primaryText)
                            }
                        }
                        .disabled(isSyncing)
                    } else {
                        Button(action: { /* Mic action */ }) {
                            Image(systemName: "mic.fill")
                                .font(.system(size: 20))
                                .foregroundColor(ScribbleFitColor.primaryText)
                        }
                    }
                }
            )
        )
    }
}

struct FeedItemRow: View {
    let item: FeedItem
    let onRetry: (String) -> Void
    
    var body: some View {
        HStack {
            if case .scribble = item {
                Spacer()
            }
            
            switch item {
            case .prompt(let prompt):
                PromptBubble(item: prompt)
            case .scribble(let scribble):
                ScribbleBubble(item: scribble, onRetry: onRetry)
            case .confirmation(let confirmation):
                ConfirmationCard(item: confirmation)
            case .insight(let insight):
                InsightBubble(item: insight)
            }
            
            if case .scribble = item {
                // No spacer on the right for scribbles
            } else {
                Spacer()
            }
        }
        .padding(.vertical, ScribbleFitSpacing.xxs)
    }
}

struct PromptBubble: View {
    let item: PromptItem
    
    var body: some View {
        Text("\(item.text) \(item.emoji)")
            .font(ScribbleFitFont.headlineSmall())
            .foregroundColor(ScribbleFitColor.primaryText)
            .frame(maxWidth: 300, alignment: .leading)
            .multilineTextAlignment(.leading)
    }
}

struct ScribbleBubble: View {
    let item: ScribbleItem
    let onRetry: (String) -> Void
    
    var body: some View {
        VStack(alignment: .trailing, spacing: 4) {
            HStack(spacing: 8) {
                Text(item.rawText)
                    .font(ScribbleFitFont.bodyLarge())
                    .foregroundColor(item.status == .failed ? .red : ScribbleFitColor.primaryText)
                
                if item.status == .pending || item.status == .processing {
                    ProgressView()
                        .controlSize(.small)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 12)
            .background(item.status == .failed ? Color.red.opacity(0.1) : ScribbleFitColor.secondaryBackground)
            .cornerRadius(ScribbleFitShape.large)
            
            if item.status == .failed {
                Button("Failed to parse. Tap to retry.") {
                    onRetry(item.id)
                }
                .font(ScribbleFitFont.labelMedium())
                .foregroundColor(.red)
            }
        }
    }
}

struct ConfirmationCard: View {
    let item: ConfirmationItem
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            VStack(alignment: .leading, spacing: 4) {
                Text(item.workout.exercises.first?.canonicalName ?? "Workout Logged")
                    .font(ScribbleFitFont.titleMedium())
                    .foregroundColor(ScribbleFitColor.primaryText)
                Text("\(item.workout.exercises.flatMap { $0.sets }.count) sets completed.")
                    .font(ScribbleFitFont.bodyMedium())
                    .foregroundColor(ScribbleFitColor.secondaryText)
            }
            
            HStack(spacing: 8) {
                Button(action: {}) {
                    Text("Confirm")
                        .font(ScribbleFitFont.titleSmall())
                        .foregroundColor(ScribbleFitColor.primaryText)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(ScribbleFitColor.primaryText.opacity(0.1))
                        .cornerRadius(ScribbleFitShape.large)
                }
                
                Button(action: {}) {
                    Text("Edit")
                        .font(ScribbleFitFont.titleSmall())
                        .foregroundColor(ScribbleFitColor.primaryText)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 10)
                        .background(ScribbleFitColor.secondaryBackground)
                        .cornerRadius(ScribbleFitShape.large)
                }
            }
        }
        .padding(16)
        .background(ScribbleFitColor.background)
        .cornerRadius(ScribbleFitShape.large)
        .overlay(
            RoundedRectangle(cornerRadius: ScribbleFitShape.large)
                .stroke(ScribbleFitColor.divider, lineWidth: 1)
        )
        .frame(maxWidth: 320)
    }
}

struct InsightBubble: View {
    let item: InsightItem
    
    var body: some View {
        HStack(spacing: 8) {
            Text(item.emoji)
            Text(item.text)
                .font(ScribbleFitFont.labelMedium())
                .foregroundColor(ScribbleFitColor.primaryText)
        }
        .padding(.horizontal, 12)
        .padding(.vertical, 8)
        .background(ScribbleFitColor.primaryText.opacity(0.05))
        .cornerRadius(ScribbleFitShape.medium)
    }
}
