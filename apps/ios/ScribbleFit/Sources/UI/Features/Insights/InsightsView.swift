import SwiftUI
import Charts

public struct InsightsView: View {
    private let store: InsightsStore
    
    public init(store: InsightsStore) {
        self.store = store
    }
    
    public var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                InsightsHeaderView()
                
                InsightsBodyView(state: store.state)
                
                InsightsFooterView()
            }
            .background(Color(.systemGroupedBackground))
        }
    }
}

private struct InsightsHeaderView: View {
    var body: some View {
        VStack(alignment: .leading) {
            Text("Insights")
                .font(.largeTitle)
                .bold()
                .padding()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color(.systemBackground))
    }
}

private struct InsightsBodyView: View {
    let state: InsightsState
    
    var body: some View {
        Group {
            if state.isLoading {
                ProgressView("Calculating statistics...")
                    .frame(maxHeight: .infinity)
            } else if state.isEmpty {
                VStack(spacing: 12) {
                    Text("No insights yet")
                        .font(.headline)
                    Text("Record at least two workouts to see your progress trends.")
                        .font(.subheadline)
                        .foregroundStyle(.secondary)
                        .multilineTextAlignment(.center)
                }
                .padding(24)
                .frame(maxHeight: .infinity)
            } else {
                List {
                    Section {
                        FrequencyGrid(frequency: state.frequency)
                    }
                    
                    Section("Training Volume") {
                        VolumeChart(points: state.volumePoints)
                            .frame(height: 200)
                    }
                    
                    Section("Muscle Group Distribution") {
                        MuscleDistributionList(distribution: state.distribution)
                    }
                }
            }
        }
    }
}

private struct FrequencyGrid: View {
    let frequency: FrequencyData?
    
    var body: some View {
        HStack {
            VStack {
                Text("\(frequency?.totalWorkouts ?? 0)")
                    .font(.title2)
                    .bold()
                Text("Total Workouts")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            .frame(maxWidth: .infinity)
            
            Divider()
            
            VStack {
                Text(String(format: "%.1f", frequency?.workoutsPerWeek ?? 0.0))
                    .font(.title2)
                    .bold()
                Text("Workouts per Week")
                    .font(.caption)
                    .foregroundStyle(.secondary)
            }
            .frame(maxWidth: .infinity)
        }
        .padding(.vertical, 8)
    }
}

private struct VolumeChart: View {
    let points: [VolumeDataPoint]
    
    var body: some View {
        Chart(points, id: \.date) { point in
            LineMark(
                x: .value("Date", point.date),
                y: .value("Volume", point.volume)
            )
            .interpolationMethod(.catmullRom)
            .symbol(Circle())
        }
    }
}

private struct MuscleDistributionList: View {
    let distribution: [MuscleGroupDistribution]
    
    var body: some View {
        ForEach(distribution, id: \.muscleGroup) { item in
            VStack(alignment: .leading, spacing: 4) {
                HStack {
                    Text(item.muscleGroup)
                        .font(.body)
                    Spacer()
                    Text("\(Int(item.percentage * 100))%")
                        .font(.body)
                        .foregroundStyle(.secondary)
                }
                
                ProgressView(value: item.percentage)
                    .tint(.blue)
            }
            .padding(.vertical, 4)
        }
    }
}

private struct InsightsFooterView: View {
    var body: some View {
        // Bottom navigation is usually handled by a TabView at the root, 
        // but for this component focus, we follow the split pattern.
        EmptyView()
    }
}
