import SwiftUI

struct ProfileView: View {
    var body: some View {
        NavigationStack {
            VStack {
                Text("User Profile")
                    .font(ScribbleFitFont.headlineMedium())
                    .foregroundColor(ScribbleFitColor.primaryText)
            }
            .navigationTitle("Profile")
            .navigationBarTitleDisplayMode(.inline)
            .background(ScribbleFitColor.background)
        }
    }
}
