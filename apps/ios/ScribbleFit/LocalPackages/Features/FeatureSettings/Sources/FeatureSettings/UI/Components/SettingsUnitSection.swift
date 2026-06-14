import SwiftUI
import CoreModel

struct SettingsUnitSection: View {
    @Binding var unit: WeightUnit

    var body: some View {
        SettingsContainer(title: String(localized: "Unit Preferences")) {
            Picker(String(localized: "Weight Unit"), selection: $unit) {
                Text(String(localized: "Kilograms (kg)")).tag(WeightUnit.kgs)
                Text(String(localized: "Pounds (lbs)")).tag(WeightUnit.lbs)
            }
            .pickerStyle(.segmented)
        }
    }
}
