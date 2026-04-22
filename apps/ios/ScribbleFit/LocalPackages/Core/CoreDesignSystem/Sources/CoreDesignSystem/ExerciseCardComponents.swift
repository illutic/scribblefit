import SwiftUI

public struct GlassCard<Content: View>: View {
    let onClick: (() -> Void)?
    let alpha: Double
    let content: Content

    public init(
        onClick: (() -> Void)? = nil,
        alpha: Double = 1.0,
        @ViewBuilder content: () -> Content
    ) {
        self.onClick = onClick
        self.alpha = alpha
        self.content = content()
    }

    public var body: some View {
        if let onClick {
            Button(action: onClick) {
                cardContent
            }
            .buttonStyle(.plain)
        } else {
            cardContent
        }
    }

    private var cardContent: some View {
        VStack(alignment: .leading, spacing: 16) {
            content
        }
        .padding(20)
        .scribbleGlass(cornerRadius: 16)
        .opacity(alpha)
    }
}

public struct ExerciseHeaderView: View {
    let name: String
    let formattedSummary: String
    let fontSize: CGFloat
    let kerning: CGFloat

    public init(
        name: String,
        formattedSummary: String,
        fontSize: CGFloat = 24,
        kerning: CGFloat = -0.5
    ) {
        self.name = name
        self.formattedSummary = formattedSummary
        self.fontSize = fontSize
        self.kerning = kerning
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(name)
                .font(.system(size: fontSize, weight: .bold))
                .kerning(kerning)
                .foregroundStyle(Color.scribblePrimary)

            Text(formattedSummary)
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)
        }
    }
}

public struct ExerciseStatsView: View {
    let estimated1RM: String?
    let intensity: String?
    let improvement: String?

    public init(
        estimated1RM: String?,
        intensity: String?,
        improvement: String? = nil
    ) {
        self.estimated1RM = estimated1RM
        self.intensity = intensity
        self.improvement = improvement
    }

    public var hasStats: Bool {
        estimated1RM != nil || intensity != nil || improvement != nil
    }

    public var body: some View {
        if hasStats {
            VStack(alignment: .leading, spacing: 12) {
                if estimated1RM != nil || intensity != nil {
                    HStack(spacing: 12) {
                        if let oneRm = estimated1RM {
                            StatCardView(label: String(localized: "EST. 1RM"), value: oneRm)
                        }
                        if let intensityVal = intensity {
                            StatCardView(label: String(localized: "INTENSITY"), value: intensityVal)
                        }
                    }
                }

                if let improvementVal = improvement {
                    HStack(spacing: 4) {
                        Image(systemName: "clock.arrow.circlepath")
                            .font(.system(size: 12))
                            .foregroundStyle(Color.scribbleMidGray)

                        Text(improvementVal.uppercased())
                            .font(.scribbleLabelMedium)
                            .fontWeight(.semibold)
                            .kerning(0.5)
                            .foregroundStyle(Color.scribbleMidGray)
                    }
                }
            }
        }
    }
}

public struct StatCardView: View {
    let label: String
    let value: String

    public init(label: String, value: String) {
        self.label = label
        self.value = value
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)

            Text(value)
                .font(.scribbleTitleMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color.scribblePrimary.opacity(0.03))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
