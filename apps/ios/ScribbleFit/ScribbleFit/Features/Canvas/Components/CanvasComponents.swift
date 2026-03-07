import SwiftUI

struct CanvasHeader: View {
    let userName: String
    
    var body: some View {
        HStack {
            Text("EVENING, \(userName.uppercased())")
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

struct ContextualInsightCard: View {
    let text: String
    
    var body: some View {
        Text(text)
            .font(ScribbleFitFont.headlineSmall())
            .foregroundColor(ScribbleFitColor.primaryText)
            .lineSpacing(4)
    }
}

struct QuickActionPills: View {
    let pills: [String]
    
    var body: some View {
        ScrollView(.horizontal, showsIndicators: false) {
            HStack(spacing: ScribbleFitSpacing.small) {
                ForEach(pills, id: \.self) { pill in
                    ScribbleFitPill(pill, action: { })
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
                    if text.isNotBlank {
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

extension String {
    var isNotBlank: Bool {
        return !self.trimmingCharacters(in: .whitespacesAndNewlines).isEmpty
    }
}
