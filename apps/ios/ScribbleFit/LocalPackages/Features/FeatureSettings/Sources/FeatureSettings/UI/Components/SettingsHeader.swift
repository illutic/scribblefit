import SwiftUI
import CoreDesignSystem

struct SettingsHeader: View {
    let onDismiss: () -> Void
    
    var body: some View {
        HStack {
            Text(String(localized: "Settings"))
                .font(.scribbleHeadlineSmall)
                .foregroundStyle(Color.scribblePrimary)
            
            Spacer()
            
            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(Color.scribbleMidGray)
                    .frame(width: 44, height: 44)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(Circle())
            }
        }
        .padding(.horizontal, 24)
    }
}
