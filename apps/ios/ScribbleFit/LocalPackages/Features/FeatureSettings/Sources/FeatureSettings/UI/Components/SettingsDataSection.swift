import SwiftUI
import CoreDesignSystem

struct SettingsDataSection: View {
    let isExporting: Bool
    let onIntent: (SettingsIntent) -> Void
    
    var body: some View {
        SettingsContainer(title: String(localized: "Data Management")) {
            VStack(spacing: 12) {
                Button(action: { onIntent(.exportData) }) {
                    HStack {
                        if isExporting {
                            ProgressView()
                                .padding(.trailing, 8)
                        }
                        Text(String(localized: "EXPORT DATA AS JSON"))
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                    }
                    .foregroundStyle(Color.scribblePrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(Capsule())
                }
                .disabled(isExporting)
                
                Button(action: { onIntent(.showClearConfirmation(true)) }) {
                    Text(String(localized: "CLEAR ALL DATA"))
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .foregroundStyle(Color.red)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.red.opacity(0.05))
                        .clipShape(Capsule())
                }
            }
        }
    }
}
