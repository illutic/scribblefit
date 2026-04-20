import SwiftUI
import CoreModel
import CoreDesignSystem

struct InsightChip: View {
    let insight: AIInsight

    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Text(insight.insightType.emoji)
                .font(.system(size: 14))
                .frame(width: 28, height: 28)
                .background(Color.scribblePrimary.opacity(0.08))
                .clipShape(Circle())

            VStack(alignment: .leading, spacing: 4) {
                Text(insight.insightType.rawValue.uppercased())
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .kerning(1)
                    .foregroundStyle(Color.scribbleMidGray)

                Text(insight.text)
                    .font(.scribbleBodyMedium)
                    .fontWeight(.medium)
                    .foregroundStyle(Color.scribblePrimary)
                    .lineSpacing(4)
            }

            Spacer()
        }
        .padding(ScribbleFitSpacing.medium)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

private extension InsightType {
    var emoji: String {
        switch self {
        case .summary: return "\u{1F525}"
        case .trend: return "\u{1F4C8}"
        case .advice: return "\u{1F4A1}"
        }
    }
}
