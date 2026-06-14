import SwiftUI
import CoreDesignSystem

struct SettingsFooter: View {
    let version: String
    
    var body: some View {
        VStack(spacing: 16) {
            Text(String(localized: "SCRIBBLEFIT VERSION \(version)"))
                .font(.scribbleLabelMedium)
                .foregroundStyle(Color.scribbleMidGray)
            
            HStack(spacing: 24) {
                Button(String(localized: "Privacy Policy")) { }
                Button(String(localized: "Terms of Service")) { }
            }
            .font(.scribbleLabelMedium)
            .foregroundStyle(Color.scribbleMidGray)
            .opacity(0.6)
        }
        .padding(.top, 24)
    }
}
