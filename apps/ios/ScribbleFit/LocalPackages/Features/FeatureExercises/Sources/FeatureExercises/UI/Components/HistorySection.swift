import SwiftUI
import CoreModel
import CoreDesignSystem

struct HistorySection: View {
    let historyCount: Int
    let onViewHistoryClick: () -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(String(localized: "HISTORY"))
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)
            
            Button(action: onViewHistoryClick) {
                HStack(spacing: 16) {
                    Image(systemName: "calendar")
                        .font(.system(size: 20))
                        .foregroundStyle(Color.scribblePrimary)
                        .frame(width: 44, height: 44)
                        .background(Color.scribbleSurfaceContainerLow)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                    
                    VStack(alignment: .leading, spacing: 2) {
                        Text(String(localized: "View all tracked sessions"))
                            .font(.scribbleTitleMedium)
                            .fontWeight(.bold)
                            .foregroundStyle(Color.scribblePrimary)
                        
                        Text("\(historyCount) \(String(localized: "total sessions recorded"))")
                            .font(.scribbleLabelMedium)
                            .foregroundStyle(Color.scribbleMidGray)
                    }
                    
                    Spacer()
                    
                    Image(systemName: "chevron.right")
                        .font(.system(size: 14, weight: .bold))
                        .foregroundStyle(Color.scribbleMidGray)
                }
                .padding(16)
                .background(Color.scribbleSurfaceContainerLow.opacity(0.4))
                .clipShape(RoundedRectangle(cornerRadius: 16))
            }
            .buttonStyle(.plain)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
