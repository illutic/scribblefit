import SwiftUI
import CoreDesignSystem

struct InsightsEmptyView: View {
    let state: InsightsState

    var body: some View {
        VStack(spacing: 32) {
            Spacer().frame(height: 40)

            Text("\u{1F331}")
                .font(.system(size: 48))

            VStack(spacing: ScribbleFitSpacing.small) {
                Text(state.emptyTitleText)
                    .font(.scribbleTitleMedium)
                    .foregroundStyle(Color.scribblePrimary)
                    .multilineTextAlignment(.center)

                Text(state.emptyStatusText)
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .kerning(1)
                    .foregroundStyle(Color.scribbleMidGray)
            }

            InsightsEmptySections(state: state)
        }
    }
}

struct InsightsEmptySections: View {
    let state: InsightsState

    var body: some View {
        VStack(spacing: ScribbleFitSpacing.large) {
            InsightsSectionContainer(title: state.thisWeekText) {
                Text(state.nothingToShowText)
                    .font(.scribbleBodyMedium)
                    .foregroundStyle(Color.scribbleMidGray)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(ScribbleFitSpacing.medium)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
            }

            InsightsSectionContainer(title: state.exercisesText) {
                Text(state.nothingToShowText)
                    .font(.scribbleBodyMedium)
                    .foregroundStyle(Color.scribbleMidGray)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .padding(ScribbleFitSpacing.medium)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(RoundedRectangle(cornerRadius: 16))
            }
        }
    }
}
