import SwiftUI
import CoreDesignSystem

struct AIOverviewCard: View {
    let text: String
    let updatedText: String

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: ScribbleFitSpacing.small) {
                Text("\u{1F525}")
                    .font(.system(size: 20))

                Spacer()

                Text(updatedText)
                    .font(.scribbleLabelMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }

            Text(text)
                .font(.scribbleBodyMedium)
                .fontWeight(.medium)
                .foregroundStyle(Color.scribblePrimary)
                .lineSpacing(4)
        }
        .padding(ScribbleFitSpacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .accessibilityIdentifier("aiOverviewCard")
    }
}

struct AIOverviewLoadingCard: View {
    @State private var isAnimating = false

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: ScribbleFitSpacing.small) {
                Circle()
                    .fill(Color.scribblePrimary.opacity(0.1))
                    .frame(width: 24, height: 24)

                Spacer()

                RoundedRectangle(cornerRadius: 4)
                    .fill(Color.scribblePrimary.opacity(0.1))
                    .frame(width: 100, height: 12)
            }

            RoundedRectangle(cornerRadius: 4)
                .fill(Color.scribblePrimary.opacity(0.1))
                .frame(height: 14)

            RoundedRectangle(cornerRadius: 4)
                .fill(Color.scribblePrimary.opacity(0.1))
                .frame(width: 200, height: 14)
        }
        .padding(ScribbleFitSpacing.medium)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 20))
        .opacity(isAnimating ? 0.5 : 1.0)
        .onAppear {
            withAnimation(.easeInOut(duration: 1.0).repeatForever(autoreverses: true)) {
                isAnimating = true
            }
        }
    }
}
