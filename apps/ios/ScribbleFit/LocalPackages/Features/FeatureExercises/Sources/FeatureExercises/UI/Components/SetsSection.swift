import SwiftUI
import CoreModel
import CoreDesignSystem

struct SetsSection: View {
    let sets: [ExerciseSet]
    let weightUnit: String

    var body: some View {
        if !sets.isEmpty {
            VStack(alignment: .leading, spacing: 16) {
                VStack(spacing: 8) {
                    ForEach(sets) { set in
                        SetItemRow(set: set, weightUnit: weightUnit)
                    }
                }
                .padding(16)
                .background(Color.scribbleSurfaceContainerLow)
                .clipShape(RoundedRectangle(cornerRadius: 12))
            }
        }
    }
}

private struct SetItemRow: View {
    let set: ExerciseSet
    let weightUnit: String

    var body: some View {
        HStack {
            HStack(spacing: 8) {
                Text("\(set.setNumber).")
                    .font(ScribbleFitTheme.typography.bodyMedium)
                    .foregroundStyle(Color.scribbleMidGray)
                    .fontWeight(.bold)

                Text(set.weight ?? 0.0 > 0 ? "\(set.weight!.formatted(.number.precision(.fractionLength(0...1)))) \(weightUnit)" : "-")
                    .font(ScribbleFitTheme.typography.bodyLarge)
                    .foregroundStyle(Color.scribblePrimary)
                    .fontWeight(.bold)

                if set.reps > 0 {
                    Text("x")
                        .font(ScribbleFitTheme.typography.bodyMedium)
                        .foregroundStyle(Color.scribbleMidGray)

                    Text("\(set.reps)")
                        .font(ScribbleFitTheme.typography.bodyLarge)
                        .foregroundStyle(Color.scribblePrimary)
                        .fontWeight(.bold)
                }
            }

            Spacer()

            if let rpe = set.rpe {
                Text("RPE \(rpe.formatted(.number.precision(.fractionLength(0...1))))")
                    .font(ScribbleFitTheme.typography.bodySmall)
                    .foregroundStyle(Color.scribbleMidGray)
            }
        }
    }
}
