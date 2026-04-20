import SwiftUI
import CoreDesignSystem

struct InsightsDataView: View {
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
