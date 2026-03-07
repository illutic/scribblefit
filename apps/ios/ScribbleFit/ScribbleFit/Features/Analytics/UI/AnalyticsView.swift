import SwiftUI

struct AnalyticsView: View {
    var body: some View {
        NavigationStack {
            VStack {
                Text("Analytics Dashboard")
                    .font(ScribbleFitFont.headlineMedium())
                    .foregroundColor(ScribbleFitColor.primaryText)
            }
            .navigationTitle("Analytics")
            .navigationBarTitleDisplayMode(.inline)
            .background(ScribbleFitColor.background)
        }
    }
}
