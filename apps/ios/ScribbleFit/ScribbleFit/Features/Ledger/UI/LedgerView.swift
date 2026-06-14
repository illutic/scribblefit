import SwiftUI

public struct LedgerView: View {
    @StateObject private var viewModel: LedgerViewModel

    public init(viewModel: LedgerViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            Spacer().frame(height: 60)

            if viewModel.history.isEmpty {
                Spacer()
                HStack {
                    Spacer()
                    Text("No history yet.")
                        .foregroundColor(.gray)
                    Spacer()
                }
                Spacer()
            } else {
                ScrollView {
                    VStack(spacing: 40) {
                        ForEach(viewModel.history) { workout in
                            WorkoutRow(workout: workout)
                        }
                    }
                    .padding(.horizontal, 24)
                    .padding(.bottom, 100)
                }
            }
        }
        .onAppear {
            viewModel.fetchHistory()
        }
    }
}

struct WorkoutRow: View {
    let workout: WorkoutHistory

    private var dateFormatter: DateFormatter {
        let formatter = DateFormatter()
        formatter.dateFormat = "EEEE, MMM d"
        return formatter
    }

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .bottom) {
                Text(dateFormatter.string(from: workout.date).uppercased())
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(.gray)
                    .kerning(1)

                Spacer()

                Text("\(Int(workout.totalVolume)) LBS")
                    .font(.system(size: 12, weight: .bold))
                    .foregroundColor(Color(hex: "101010"))
            }

            if let location = workout.location {
                Text(location)
                    .font(.system(size: 14))
                    .foregroundColor(.gray)
            }

            VStack(alignment: .leading, spacing: 12) {
                ForEach(workout.exercises) { exercise in
                    ExerciseItem(exercise: exercise)
                }
            }

            Rectangle()
                .fill(Color(hex: "F7F7F8"))
                .frame(height: 1)
                .padding(.top, 8)
        }
    }
}

struct ExerciseItem: View {
    let exercise: ExerciseHistory

    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(exercise.canonicalName)
                .font(.system(size: 18, weight: .semibold))
                .foregroundColor(Color(hex: "101010"))

            ForEach(exercise.sets) { set in
                HStack(spacing: 4) {
                    Text("SET: \(Int(set.weight)) x \(set.reps)")
                        .font(.system(size: 14, design: .monospaced))
                        .foregroundColor(Color(hex: "101010"))

                    if let rpe = set.rpe {
                        Text("@ RPE \(String(format: "%.1f", rpe))")
                            .font(.system(size: 12))
                            .foregroundColor(.gray)
                    }
                }
            }
        }
    }
}
