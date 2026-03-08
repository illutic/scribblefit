import SwiftUI

public struct LedgerView: View {
    @ObservedObject private var viewModel: LedgerViewModel

    public init(viewModel: LedgerViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        NavigationStack {
            List(viewModel.history) { workout in
                WorkoutHistoryRow(workout: workout)
            }
            .listStyle(.plain)
            .navigationTitle("Workouts")
            .task { await viewModel.fetchHistory() }
        }
        .background(ScribbleFitColor.background)
    }
}

private struct WorkoutHistoryRow: View {
    let workout: WorkoutHistory
    private static let formatter: DateFormatter = {
        let f = DateFormatter()
        f.dateStyle = .medium
        return f
    }()

    var body: some View {
        VStack(alignment: .leading, spacing: ScribbleFitSpacing.small) {
            Text(Self.formatter.string(from: workout.date))
                .font(.headline)
                .foregroundStyle(ScribbleFitColor.richBlack)
            Text("\(workout.exercises.count) exercises · \(Int(workout.totalVolume)) lbs")
                .font(.subheadline)
                .foregroundStyle(ScribbleFitColor.midGray)
        }
        .padding(.vertical, ScribbleFitSpacing.small)
    }
}
