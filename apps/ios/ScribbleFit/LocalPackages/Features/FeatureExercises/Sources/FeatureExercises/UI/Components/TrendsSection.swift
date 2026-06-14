import SwiftUI
import CoreModel
import CoreDesignSystem

struct TrendsSection: View {
    let trends: ExerciseTrends
    let weightUnit: String
    let onViewAllClick: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                Text(String(localized: "TRENDS"))
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .kerning(1)
                    .foregroundStyle(Color.scribbleMidGray)

                Spacer()

                Button(action: onViewAllClick) {
                    HStack(spacing: 4) {
                        Text(String(localized: "VIEW ALL"))
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)

                        Image(systemName: "chevron.right")
                            .font(.system(size: 10, weight: .bold))
                    }
                    .foregroundStyle(Color.scribblePrimary)
                }
            }

            VStack(spacing: 20) {
                TrendItem(
                    label: String(localized: "Current 1RM"),
                    value: "\(Int(trends.current1RM))\(weightUnit)",
                    direction: trends.trendDirection
                )

                TrendItem(
                    label: String(localized: "Intensity"),
                    value: "\(Int(trends.intensity * 100))%",
                    direction: .stable // Intensity doesn't have a trend direction in this view
                )

                TrendItem(
                    label: String(localized: "Weight vs Last"),
                    value: "\(trends.improvement >= 0 ? "+" : "")\(Int(trends.improvement * 100))%",
                    direction: trends.trendDirection
                )

                TrendItem(
                    label: String(localized: "Last Volume"),
                    value: "\(Int(trends.lastVolume))\(weightUnit)",
                    direction: trends.lastVolumeTrend
                )
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct TrendItem: View {
    let label: String
    let value: String
    let direction: TrendDirection

    var body: some View {
        HStack {
            Text(label)
                .font(.scribbleBodyMedium)
                .fontWeight(.medium)
                .foregroundStyle(Color.scribblePrimary)

            Spacer()

            HStack(spacing: 12) {
                Text(value)
                    .font(.scribbleBodyMedium)
                    .fontWeight(.bold)
                    .foregroundStyle(Color.scribblePrimary)

                TrendBadge(direction: direction)
            }
        }
    }
}

private struct TrendBadge: View {
    let direction: TrendDirection

    var body: some View {
        let (text, color) = {
            switch direction {
            case .improving: return (String(localized: "IMPROVING"), Color.scribbleSuccess)
            case .stable: return (String(localized: "STABLE"), Color.scribbleMidGray)
            case .plateaued: return (String(localized: "PLATEAUED"), Color.scribbleWarning)
            case .declining: return (String(localized: "DECLINING"), Color.scribbleError)
            }
        }()

        Text(text)
            .font(.system(size: 10, weight: .bold))
            .foregroundStyle(color)
            .padding(.horizontal, 10)
            .padding(.vertical, 4)
            .background(color.opacity(0.1))
            .clipShape(Capsule())
    }
}
