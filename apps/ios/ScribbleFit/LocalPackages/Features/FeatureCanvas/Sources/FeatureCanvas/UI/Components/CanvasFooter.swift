import SwiftUI
import CoreDesignSystem

struct CanvasFooter: View {
    @Binding var text: String
    let isSending: Bool
    let onSend: () -> Void
    let onManualAdd: () -> Void

    var body: some View {
        HStack(alignment: .bottom, spacing: 12) {
            ScribbleInputBar(
                text: $text,
                placeholder: String(localized: "Enter workout (e.g., Bench 100kg 3x5)"),
                onSend: onSend
            )
            .disabled(isSending)
            .opacity(isSending ? 0.6 : 1.0)

            Button(action: onManualAdd) {
                Image(systemName: "plus.circle.fill")
                    .font(.system(size: 24))
                    .foregroundStyle(Color.scribblePrimary)
                    .frame(height: 48)
            }
            .accessibilityIdentifier("manualAddButton")
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 16)
        .background(Color.clear)
    }
}
