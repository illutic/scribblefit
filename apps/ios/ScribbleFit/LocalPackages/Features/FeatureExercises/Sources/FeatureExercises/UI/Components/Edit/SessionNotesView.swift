import SwiftUI
import CoreDesignSystem

public struct SessionNotesView: View {
    @Binding public var notes: String
    public let label: String
    public let placeholder: String
    
    public init(notes: Binding<String>, label: String, placeholder: String) {
        self._notes = notes
        self.label = label
        self.placeholder = placeholder
    }
    
    public var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label.uppercased())
                .font(.scribbleLabelMedium.bold())
                .foregroundColor(.scribbleMidGray)
            
            ZStack(alignment: .topLeading) {
                if notes.isEmpty {
                    Text(placeholder)
                        .font(.scribbleBodyMedium)
                        .foregroundColor(.scribbleMidGray.opacity(0.5))
                        .padding(.horizontal, 16)
                        .padding(.vertical, 12)
                }
                
                TextEditor(text: $notes)
                    .font(.scribbleBodyMedium)
                    .foregroundColor(.scribblePrimary)
                    .scrollContentBackground(.hidden)
                    .cornerRadius(12)
                    .frame(minHeight: 100)
            }
        }
    }
}
