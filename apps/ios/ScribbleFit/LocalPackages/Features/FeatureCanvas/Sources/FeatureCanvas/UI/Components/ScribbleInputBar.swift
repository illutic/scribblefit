import SwiftUI
import CoreDesignSystem

struct ScribbleInputBar: View {
    @Binding var text: String
    let placeholder: String
    let onSend: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            TextField(placeholder, text: $text)
                .font(.scribbleBodyMedium)
                .padding(.leading, 16)
                .submitLabel(.send)
                .onSubmit(onSend)
            
            if !text.isEmpty {
                Button(action: onSend) {
                    Image(systemName: "arrow.up.circle.fill")
                        .font(.system(size: 32))
                        .foregroundStyle(Color.scribblePrimary)
                }
                .padding(.trailing, 4)
                .transition(.scale.combined(with: .opacity))
            }
        }
        .frame(height: 52)
        .scribbleGlass(cornerRadius: 24)
        .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: text.isEmpty)
    }
}
