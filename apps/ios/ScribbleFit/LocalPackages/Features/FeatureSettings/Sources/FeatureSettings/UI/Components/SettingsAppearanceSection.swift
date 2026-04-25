import SwiftUI
import CoreModel

struct SettingsAppearanceSection: View {
    @Binding var theme: ThemePreference
    
    var body: some View {
        SettingsContainer(title: String(localized: "Appearance")) {
            Picker(String(localized: "Theme"), selection: $theme) {
                Text(String(localized: "Light")).tag(ThemePreference.light)
                Text(String(localized: "Dark")).tag(ThemePreference.dark)
                Text(String(localized: "System")).tag(ThemePreference.system)
            }
            .pickerStyle(.segmented)
        }
    }
}
