import SwiftUI
import CoreDesignSystem

struct StatsRow: View {
    let sessions: Int
    let volume: Float
    let exerciseCount: Int
    let sessionsLabel: String
    let volumeLabel: String
    let exerciseCountLabel: String

    var body: some View {
        HStack(spacing: 8) {
            StatItem(
                value: "\(sessions)",
                label: sessionsLabel
            )

            StatItem(
                value: formatVolume(volume),
                label: volumeLabel
            )

            StatItem(
                value: "\(exerciseCount)",
                label: exerciseCountLabel
            )
        }
        .accessibilityIdentifier("statsRow")
    }

    private func formatVolume(_ volume: Float) -> String {
        if volume >= 1_000_000 {
            return String(format: "%.1fM", volume / 1_000_000)
        }
        if volume >= 1000 {
            return String(format: "%.1fk", volume / 1000)
        }
        return String(format: "%.0f", volume)
    }
}

struct StatItem: View {
    let value: String
    let label: String

    var body: some View {
        VStack(spacing: 4) {
            Text(value)
                .font(.scribbleHeadlineSmall)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)

            Text(label)
                .font(.scribbleLabelMedium)
                .foregroundStyle(Color.scribbleMidGray)
        }
        .frame(maxWidth: .infinity)
        .padding(ScribbleFitSpacing.medium)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
