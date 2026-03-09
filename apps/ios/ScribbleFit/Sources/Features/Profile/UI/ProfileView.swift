import SwiftUI

public struct ProfileView: View {
    @ObservedObject private var settingsViewModel: SettingsViewModel

    public init(settingsViewModel: SettingsViewModel) {
        self.settingsViewModel = settingsViewModel
    }

    public var body: some View {
        SettingsView(viewModel: settingsViewModel)
    }
}
