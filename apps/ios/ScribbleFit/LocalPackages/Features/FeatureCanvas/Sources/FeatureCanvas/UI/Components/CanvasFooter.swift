import SwiftUI
import CoreDesignSystem

struct CanvasFooter: View {
    @Binding var text: String
    let isSending: Bool
    let onSend: () -> Void
    
    var body: some View {
        HStack(alignment: .bottom, spacing: 12) {
            ScribbleInputBar(
                text: $text,
                placeholder: String(localized: "Enter workout (e.g., Bench 100kg 3x5)"),
                onSend: onSend
            )
            .disabled(isSending)
            .opacity(isSending ? 0.6 : 1.0)
        }
        .padding(.horizontal, 24)
        .padding(.vertical, 16)
        .background(Color.clear) // Transparent background as it overlays content
    }
}
