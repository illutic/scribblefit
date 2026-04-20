import SwiftUI
import CoreModel
import CoreDesignSystem

struct MuscleDistributionBars: View {
    let distribution: [MuscleGroupDistribution]

    var body: some View {
        VStack(spacing: 12) {
            ForEach(distribution) { item in
                MuscleDistributionRow(
                    muscleGroup: item.muscleGroup,
                    percentage: item.percentage
                )
            }
        }
        .padding(ScribbleFitSpacing.medium)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .accessibilityIdentifier("muscleDistribution")
    }
}

struct MuscleDistributionRow: View {
    let muscleGroup: String
    let percentage: Float

    var body: some View {
        VStack(alignment: .leading, spacing: 6) {
            HStack {
                Text(muscleGroup)
                    .font(.scribbleBodyMedium)
                    .fontWeight(.medium)
                    .foregroundStyle(Color.scribblePrimary)

                Spacer()

                Text(String(format: "%.0f%%", percentage))
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .foregroundStyle(Color.scribbleMidGray)
            }

            GeometryReader { geometry in
                ZStack(alignment: .leading) {
                    RoundedRectangle(cornerRadius: 3)
                        .fill(Color.scribbleSurfaceContainerHigh)
                        .frame(height: 6)

                    RoundedRectangle(cornerRadius: 3)
                        .fill(Color.scribblePrimary)
                        .frame(width: geometry.size.width * CGFloat(percentage / 100.0), height: 6)
                }
            }
            .frame(height: 6)
        }
    }
}
