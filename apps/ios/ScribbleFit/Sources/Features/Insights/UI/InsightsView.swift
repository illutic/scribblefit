import SwiftUI
import Charts
#if SWIFT_PACKAGE
import CoreModel
import CoreDesignSystem
#endif

public struct InsightsView: View {
    @Bindable var store: InsightsStore

    public init(store: InsightsStore) {
        self.store = store
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()

                ScrollView {
                    VStack(spacing: ScribbleFitSpacing.large) {
                        if store.state.isLoading {
                            InsightsLoadingView(state: store.state)
                        } else if store.state.isEmpty {
                            InsightsEmptyView(state: store.state)
                        } else {
                            InsightsDataView(state: store.state)
                        }
                    }
                    .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                    .padding(.vertical, ScribbleFitSpacing.large)
                }
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(store.state.titleText)
                        .font(.scribbleTitleMedium)
                        .fontWeight(.semibold)
                        .foregroundStyle(Color.scribblePrimary)
                }
            }
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .refreshable {
                store.onIntent(.refresh)
            }
        }
    }
}

// MARK: - Loading State

private struct InsightsLoadingView: View {
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

// MARK: - Empty State

private struct InsightsEmptyView: View {
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

private struct InsightsEmptySections: View {
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

// MARK: - Data State

private struct InsightsDataView: View {
    let state: InsightsState

    var body: some View {
        VStack(spacing: ScribbleFitSpacing.large) {
            // AI Overview Card
            if state.isGeneratingAI {
                AIOverviewLoadingCard()
            } else if let overview = state.aiOverview, let firstInsight = overview.insights.first {
                AIOverviewCard(
                    text: firstInsight.text,
                    updatedText: state.updatedJustNowText
                )
            }

            // Insight Chips (non-summary insights, right after AI card)
            if let overview = state.aiOverview {
                let additionalInsights = Array(overview.insights.dropFirst())
                if !additionalInsights.isEmpty {
                    VStack(spacing: 12) {
                        ForEach(additionalInsights) { insight in
                            InsightChip(insight: insight)
                        }
                    }
                }
            }

            // Stats Row
            if let frequency = state.frequency {
                StatsRow(
                    sessions: frequency.totalWorkouts,
                    volume: state.totalVolume,
                    exerciseCount: state.totalExercises,
                    sessionsLabel: state.sessionsLabel,
                    volumeLabel: state.volumeLabel,
                    exerciseCountLabel: state.exerciseCountLabel
                )
            }

            // Muscle Distribution
            if !state.distribution.isEmpty {
                InsightsSectionContainer(title: state.muscleDistributionLabel) {
                    MuscleDistributionBars(distribution: state.distribution)
                }
            }

            // Volume Chart
            if !state.volumePoints.isEmpty {
                InsightsSectionContainer(title: state.weeklyVolumeLabel) {
                    VolumeChart(
                        points: state.volumePoints,
                        weightUnitLabel: state.weightUnitLabel
                    )
                }
            }
        }
    }
}

// MARK: - AI Overview Card

private struct AIOverviewCard: View {
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

private struct AIOverviewLoadingCard: View {
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

// MARK: - Insight Chip

private struct InsightChip: View {
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

// MARK: - Stats Row

private struct StatsRow: View {
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

private struct StatItem: View {
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

// MARK: - Volume Chart

private struct VolumeChart: View {
    let points: [VolumeDataPoint]
    let weightUnitLabel: String

    var body: some View {
        Chart(points) { point in
            LineMark(
                x: .value("Date", point.date, unit: .day),
                y: .value("Volume", point.volume)
            )
            .foregroundStyle(Color.scribblePrimary)
            .interpolationMethod(.catmullRom)

            AreaMark(
                x: .value("Date", point.date, unit: .day),
                y: .value("Volume", point.volume)
            )
            .foregroundStyle(
                LinearGradient(
                    colors: [Color.scribblePrimary.opacity(0.2), Color.scribblePrimary.opacity(0.0)],
                    startPoint: .top,
                    endPoint: .bottom
                )
            )
            .interpolationMethod(.catmullRom)

            PointMark(
                x: .value("Date", point.date, unit: .day),
                y: .value("Volume", point.volume)
            )
            .foregroundStyle(Color.scribblePrimary)
            .symbolSize(24)
        }
        .chartYAxisLabel(weightUnitLabel)
        .chartXAxis {
            AxisMarks(values: .stride(by: .day, count: 7)) { _ in
                AxisGridLine()
                AxisValueLabel(format: .dateTime.month(.abbreviated).day())
            }
        }
        .frame(height: 180)
        .padding(ScribbleFitSpacing.medium)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .accessibilityIdentifier("volumeChart")
    }
}

// MARK: - Muscle Distribution

private struct MuscleDistributionBars: View {
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

private struct MuscleDistributionRow: View {
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

// MARK: - Insight Type Emoji

private extension InsightType {
    var emoji: String {
        switch self {
        case .summary: return "\u{1F525}"
        case .trend: return "\u{1F4C8}"
        case .advice: return "\u{1F4A1}"
        }
    }
}

// MARK: - Reusable Section Container

private struct InsightsSectionContainer<Content: View>: View {
    let title: String
    let content: () -> Content

    init(title: String, @ViewBuilder content: @escaping () -> Content) {
        self.title = title
        self.content = content
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(title.uppercased())
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)

            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}
