import SwiftUI
import CoreDesignSystem

struct SettingsContainer<Content: View>: View {
    let title: String
    let content: () -> Content
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(title.uppercased())
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)
            
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
