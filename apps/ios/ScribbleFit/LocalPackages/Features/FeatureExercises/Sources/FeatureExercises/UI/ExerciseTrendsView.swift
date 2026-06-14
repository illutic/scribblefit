import SwiftUI
import Charts
import CoreModel
import CoreDesignSystem

public struct ExerciseTrendsView: View {
    @Bindable var store: ExerciseTrendsStore

    public init(store: ExerciseTrendsStore) {
        self.store = store
    }

    public var body: some View {
        ZStack {
            Color.scribbleBackground.ignoresSafeArea()

            if store.state.isLoading && store.state.oneRMDataPoints.isEmpty {
                ProgressView()
                    .tint(Color.scribblePrimary)
            } else if store.state.oneRMDataPoints.isEmpty && store.state.volumeDataPoints.isEmpty && !store.state.isLoading {
                VStack(spacing: ScribbleFitSpacing.medium) {
                    Image(systemName: "chart.line.uptrend.xyaxis")
                        .font(.system(size: 48))
                        .foregroundStyle(Color.scribbleMidGray)

                    Text(store.state.emptyDataMessage)
                        .font(.scribbleTitleMedium)
                        .foregroundStyle(Color.scribbleMidGray)
                }
            } else {
                ScrollView {
                    VStack(spacing: ScribbleFitSpacing.xl) {
                        periodPicker

                        trendChartSection(
                            title: store.state.oneRMSectionTitle,
                            data: store.state.oneRMDataPoints,
                            insights: store.state.oneRMInsights,
                            unit: store.state.weightUnitLabel
                        )

                        trendChartSection(
                            title: store.state.volumeSectionTitle,
                            data: store.state.volumeDataPoints,
                            insights: store.state.volumeInsights,
                            unit: store.state.weightUnitLabel
                        )

                        Spacer()
                            .frame(height: 40)
                    }
                    .padding(.vertical, ScribbleFitSpacing.large)
                    .padding(.horizontal, ScribbleFitSpacing.screenPadding)
                }
            }
        }
        .navigationTitle(store.state.navigationTitle)
        #if os(iOS)
        .navigationBarTitleDisplayMode(.inline)
        #endif
        .task {
            store.onIntent(.loadData)
        }
    }

    private var periodPicker: some View {
        Picker(store.state.periodPickerLabel, selection: $store.state.selectedPeriod) {
            ForEach([TrendPeriod.oneMonth, .threeMonths, .sixMonths, .oneYear, .all], id: \.self) { period in
                Text(period.rawValue).tag(period)
            }
        }
        .pickerStyle(.segmented)
    }

    @ViewBuilder
    private func trendChartSection(
        title: String,
        data: [TrendDataPoint],
        insights: TrendInsights?,
        unit: String
    ) -> some View {
        VStack(alignment: .leading, spacing: ScribbleFitSpacing.medium) {
            HStack {
                Text(title.uppercased())
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .kerning(1)
                    .foregroundStyle(Color.scribbleMidGray)

                Spacer()

                if let insights = insights {
                    trendBadge(direction: insights.trendDirection, percentage: insights.percentageChange)
                }
            }

            VStack(alignment: .leading, spacing: ScribbleFitSpacing.small) {
                if let lastValue = data.last?.value {
                    HStack(alignment: .bottom, spacing: 4) {
                        Text("\(Int(lastValue))")
                            .font(.scribbleHeadlineSmall)
                            .fontWeight(.bold)
                            .foregroundStyle(Color.scribblePrimary)

                        Text(unit)
                            .font(.scribbleBodyMedium)
                            .foregroundStyle(Color.scribbleMidGray)
                            .padding(.bottom, 4)

                        Spacer()

                        if let pb = insights?.personalBest {
                            VStack(alignment: .trailing, spacing: 2) {
                                Text(store.state.personalBestLabel)
                                    .font(.system(size: 8, weight: .bold))
                                    .foregroundStyle(Color.scribbleMidGray)
                                Text("\(Int(pb)) \(unit)")
                                    .font(.scribbleLabelSmall)
                                    .foregroundStyle(Color.scribblePrimary)
                            }
                        }
                    }
                }

                Chart {
                    ForEach(data) { point in
                        LineMark(
                            x: .value("Date", point.date),
                            y: .value("Value", point.value)
                        )
                        .interpolationMethod(.catmullRom)
                        .foregroundStyle(Color.scribblePrimary)
                        .lineStyle(StrokeStyle(lineWidth: 3))

                        AreaMark(
                            x: .value("Date", point.date),
                            y: .value("Value", point.value)
                        )
                        .interpolationMethod(.catmullRom)
                        .foregroundStyle(
                            LinearGradient(
                                colors: [Color.scribblePrimary.opacity(0.1), Color.scribblePrimary.opacity(0)],
                                startPoint: .top,
                                endPoint: .bottom
                            )
                        )

                        PointMark(
                            x: .value("Date", point.date),
                            y: .value("Value", point.value)
                        )
                        .foregroundStyle(Color.scribblePrimary)
                        .symbolSize(30)
                    }
                }
                .chartXAxis {
                    AxisMarks { _ in
                        AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                            .foregroundStyle(Color.scribbleMidGray.opacity(0.2))
                        AxisValueLabel(format: .dateTime.month(.abbreviated).day())
                            .font(.scribbleLabelSmall)
                            .foregroundStyle(Color.scribbleMidGray)
                    }
                }
                .chartYAxis {
                    AxisMarks { value in
                        AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5))
                            .foregroundStyle(Color.scribbleMidGray.opacity(0.2))
                        AxisValueLabel {
                            if let floatValue = value.as(Float.self) {
                                Text("\(Int(floatValue))")
                                    .font(.scribbleLabelSmall)
                                    .foregroundStyle(Color.scribbleMidGray)
                            }
                        }
                    }
                }
                .frame(height: 200)
            }
            .padding(ScribbleFitSpacing.large)
            .background(Color.scribbleSurfaceContainerLow)
            .clipShape(RoundedRectangle(cornerRadius: ScribbleFitShape.large))
        }
    }

    @ViewBuilder
    private func trendBadge(direction: TrendDirection, percentage: Float) -> some View {
        let isPositive = {
            switch direction {
            case .improving, .stable: return true
            case .plateaued, .declining: return false
            }
        }()

        Text(store.state.badgeText(for: direction, percentage: percentage))
            .font(.system(size: 10, weight: .bold))
            .foregroundStyle(isPositive ? Color.scribblePrimary : Color.scribbleMidGray)
            .padding(.horizontal, 10)
            .padding(.vertical, 4)
            .background(Color.scribbleMidGray.opacity(0.1))
            .clipShape(Capsule())
    }
}

private extension ScribbleFitSpacing {
    static let xl: CGFloat = 32
}
