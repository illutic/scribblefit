import SwiftUI
import Charts

public struct InsightsView: View {
    private let store: InsightsStore
    @Environment(\.scribbleFitColors) var colors
    
    public init(store: InsightsStore) {
        self.store = store
    }
    
    public var body: some View {
        ZStack {
            colors.background
                .ignoresSafeArea()
            
            VStack(spacing: 0) {
                InsightsHeaderView(title: "Insights")
                
                InsightsBodyView(state: store.state)
                
                InsightsFooterView()
            }
        }
    }
}

private struct InsightsHeaderView: View {
    let title: String
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        HStack {
            Text(title)
                .font(.system(size: 22, weight: .bold))
                .foregroundColor(colors.richBlack)
            Spacer()
        }
        .padding(.horizontal, 16)
        .padding(.vertical, 12)
        .background(colors.background)
    }
}

private struct InsightsBodyView: View {
    let state: InsightsState
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        Group {
            if state.isLoading {
                VStack {
                    ProgressView()
                        .tint(colors.richBlack)
                    Spacer().frame(height: 12)
                    Text("Calculating statistics...")
                        .font(.system(size: 14))
                        .foregroundColor(colors.midGray)
                }
                .frame(maxHeight: .infinity)
            } else if state.isEmpty {
                VStack(spacing: 12) {
                    Text("No insights yet")
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(colors.richBlack)
                    Text("Record at least two workouts to see your progress trends.")
                        .font(.system(size: 15))
                        .foregroundColor(colors.midGray)
                        .multilineTextAlignment(.center)
                        .padding(.horizontal, 32)
                }
                .frame(maxHeight: .infinity)
            } else {
                ScrollView {
                    VStack(spacing: 24) {
                        AIOverviewSection(
                            isGenerating: state.isGeneratingAI,
                            overview: state.aiOverview
                        )
                        
                        FrequencySection(frequency: state.frequency)
                        
                        VolumeSection(points: state.volumePoints)
                        
                        MuscleDistributionSection(distribution: state.distribution)
                    }
                    .padding(16)
                }
            }
        }
    }
}

private struct AIOverviewSection: View {
    let isGenerating: Bool
    let overview: AIOverview?
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            Text("AI Progress Overview")
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(colors.richBlack)
            
            if isGenerating {
                AIOverviewLoading()
            } else if let overview = overview {
                AIOverviewCard(overview: overview)
            }
        }
    }
}

private struct AIOverviewLoading: View {
    @Environment(\.scribbleFitColors) var colors
    @State private var opacity: Double = 0.3
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack(spacing: 8) {
                ProgressView()
                    .tint(colors.richBlack)
                    .scaleEffect(0.7)
                Text("Generating your summary...")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(colors.midGray)
            }
            
            VStack(alignment: .leading, spacing: 8) {
                RoundedRectangle(cornerRadius: 4)
                    .fill(colors.softGray)
                    .frame(height: 16)
                    .opacity(opacity)
                
                RoundedRectangle(cornerRadius: 4)
                    .fill(colors.softGray)
                    .frame(width: 200, height: 16)
                    .opacity(opacity)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(colors.background)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(colors.lightGray, lineWidth: 1)
        )
        .onAppear {
            withAnimation(.easeInOut(duration: 1.0).repeatForever(autoreverses: true)) {
                opacity = 0.7
            }
        }
    }
}

private struct AIOverviewCard: View {
    let overview: AIOverview
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text(overview.summary)
                .font(.system(size: 15))
                .foregroundColor(colors.richBlack)
            
            Divider()
                .background(colors.lightGray)
            
            VStack(alignment: .leading, spacing: 4) {
                Text("Trends")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(colors.midGray)
                Text(overview.trends)
                    .font(.system(size: 14))
                    .foregroundColor(colors.strongGray)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text("Advice")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(colors.midGray)
                Text(overview.advice)
                    .font(.system(size: 14))
                    .foregroundColor(colors.strongGray)
            }
        }
        .padding(16)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(colors.background)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(colors.lightGray, lineWidth: 1)
        )
    }
}

private struct FrequencySection: View {
    let frequency: FrequencyData?
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        HStack {
            VStack(spacing: 4) {
                Text("\(frequency?.totalWorkouts ?? 0)")
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(colors.richBlack)
                Text("Total Workouts")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(colors.midGray)
            }
            .frame(maxWidth: .infinity)
            
            Rectangle()
                .fill(colors.lightGray)
                .frame(width: 1, height: 40)
            
            VStack(spacing: 4) {
                Text(String(format: "%.1f", frequency?.workoutsPerWeek ?? 0.0))
                    .font(.system(size: 24, weight: .bold))
                    .foregroundColor(colors.richBlack)
                Text("Workouts per Week")
                    .font(.system(size: 12, weight: .medium))
                    .foregroundColor(colors.midGray)
            }
            .frame(maxWidth: .infinity)
        }
        .padding(16)
        .background(colors.background)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(colors.lightGray, lineWidth: 1)
        )
    }
}

private struct VolumeSection: View {
    let points: [VolumeDataPoint]
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Training Volume")
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(colors.richBlack)
            
            Chart(points, id: \.date) { point in
                LineMark(
                    x: .value("Date", point.date),
                    y: .value("Volume", point.volume)
                )
                .foregroundStyle(colors.blue)
                .interpolationMethod(.catmullRom)
                
                AreaMark(
                    x: .value("Date", point.date),
                    y: .value("Volume", point.volume)
                )
                .foregroundStyle(LinearGradient(
                    colors: [colors.blue.opacity(0.3), colors.blue.opacity(0.0)],
                    startPoint: .top,
                    endPoint: .bottom
                ))
                .interpolationMethod(.catmullRom)
            }
            .frame(height: 180)
            .chartXAxis {
                AxisMarks(values: .stride(by: .day, count: 7)) { _ in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5)).foregroundStyle(colors.lightGray)
                    AxisTick(stroke: StrokeStyle(lineWidth: 0.5)).foregroundStyle(colors.lightGray)
                    AxisValueLabel(format: .dateTime.month().day(), centered: true)
                        .foregroundStyle(colors.midGray)
                }
            }
            .chartYAxis {
                AxisMarks { value in
                    AxisGridLine(stroke: StrokeStyle(lineWidth: 0.5)).foregroundStyle(colors.lightGray)
                    AxisValueLabel().foregroundStyle(colors.midGray)
                }
            }
        }
        .padding(16)
        .background(colors.background)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(colors.lightGray, lineWidth: 1)
        )
    }
}

private struct MuscleDistributionSection: View {
    let distribution: [MuscleGroupDistribution]
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Muscle Group Distribution")
                .font(.system(size: 16, weight: .bold))
                .foregroundColor(colors.richBlack)
            
            VStack(spacing: 12) {
                ForEach(distribution, id: \.muscleGroup) { item in
                    VStack(alignment: .leading, spacing: 6) {
                        HStack {
                            Text(item.muscleGroup)
                                .font(.system(size: 14, weight: .medium))
                                .foregroundColor(colors.richBlack)
                            Spacer()
                            Text("\(Int(item.percentage * 100))%")
                                .font(.system(size: 12, weight: .bold))
                                .foregroundColor(colors.midGray)
                        }
                        
                        GeometryReader { geometry in
                            ZStack(alignment: .leading) {
                                Capsule()
                                    .fill(colors.softGray)
                                    .frame(height: 8)
                                
                                Capsule()
                                    .fill(colors.richBlack)
                                    .frame(width: geometry.size.width * CGFloat(item.percentage), height: 8)
                            }
                        }
                        .frame(height: 8)
                    }
                }
            }
        }
        .padding(16)
        .background(colors.background)
        .cornerRadius(12)
        .overlay(
            RoundedRectangle(cornerRadius: 12)
                .stroke(colors.lightGray, lineWidth: 1)
        )
    }
}

private struct InsightsFooterView: View {
    var body: some View {
        Spacer().frame(height: 1)
    }
}
