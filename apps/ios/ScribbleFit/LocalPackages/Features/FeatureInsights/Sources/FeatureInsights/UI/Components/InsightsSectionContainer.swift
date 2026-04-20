import SwiftUI
import CoreDesignSystem

struct InsightsSectionContainer<Content: View>: View {
    let title: String
    let content: () -> Content

    init(title: String, @ViewBuilder content: @escaping () -> Content) {
        self.title = title
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
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
