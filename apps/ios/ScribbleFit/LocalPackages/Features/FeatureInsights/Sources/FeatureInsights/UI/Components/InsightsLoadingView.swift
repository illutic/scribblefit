import SwiftUI
import CoreDesignSystem

struct InsightsLoadingView: View {
    let state: InsightsState
    @State private var isPulsing = false

    var body: some View {
        VStack(spacing: 32) {
            Spacer().frame(height: 40)

            Text("\u{23F3}")
                .font(.system(size: 48))
                .opacity(isPulsing ? 0.5 : 1.0)

            VStack(spacing: ScribbleFitSpacing.small) {
                Text(state.loadingTitleText)
                    .font(.scribbleTitleMedium)
                    .foregroundStyle(Color.scribblePrimary)
                    .multilineTextAlignment(.center)

                Text(state.loadingSubtitleText)
                    .font(.scribbleBodyMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }

            InsightsEmptySections(state: state)
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 2.0).repeatForever(autoreverses: true)) {
                isPulsing = true
            }
        }
    }
}
