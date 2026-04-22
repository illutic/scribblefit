import SwiftUI
import CoreModel
import CoreDesignSystem

struct WeeklyStatsCard: View {
    let stats: WeeklyStats
    let weightUnit: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(String(localized: "WEEKLY PERFORMANCE"))
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)
            
            HStack(spacing: 0) {
                StatItem(
                    label: String(localized: "Activity"),
                    value: "\(stats.sessionsThisWeek)",
                    unit: String(localized: "sessions")
                )
                .frame(maxWidth: .infinity, alignment: .leading)
                
                StatItem(
                    label: String(localized: "Volume"),
                    value: "\(Int(stats.totalVolumeThisWeek))",
                    unit: weightUnit
                )
                .frame(maxWidth: .infinity, alignment: .leading)
                
                StatItem(
                    label: String(localized: "Max Weight"),
                    value: "\(Int(stats.maxWeightThisWeek))",
                    unit: weightUnit
                )
                .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(24)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.scribbleSurfaceContainerLow.opacity(0.4))
        .clipShape(RoundedRectangle(cornerRadius: 16))
    }
}

private struct StatItem: View {
    let label: String
    let value: String
    let unit: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.scribbleLabelMedium)
                .foregroundStyle(Color.scribbleMidGray)
            
            HStack(alignment: .bottom, spacing: 4) {
                Text(value)
                    .font(.scribbleHeadlineSmall)
                    .fontWeight(.bold)
                    .foregroundStyle(Color.scribblePrimary)
                
                Text(unit)
                    .font(.scribbleLabelMedium)
                    .foregroundStyle(Color.scribbleMidGray)
                    .padding(.bottom, 4)
            }
        }
    }
}
