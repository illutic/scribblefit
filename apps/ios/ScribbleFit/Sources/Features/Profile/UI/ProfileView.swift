import SwiftUI

public struct ProfileView: View {
    @ObservedObject private var settingsViewModel: SettingsViewModel

    public init(settingsViewModel: SettingsViewModel) {
        self.settingsViewModel = settingsViewModel
    }

    public var body: some View {
        NavigationStack {
            VStack(alignment: .leading, spacing: ScribbleFitSpacing.medium) {
                Text("George")
                    .font(.system(size: 28, weight: .bold))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                NavigationLink("Settings", destination: SettingsView(viewModel: settingsViewModel))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                Spacer()
            }
            .padding(ScribbleFitSpacing.screenPadding)
            .navigationTitle("Profile")
#if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
#endif
        }
    }
}
