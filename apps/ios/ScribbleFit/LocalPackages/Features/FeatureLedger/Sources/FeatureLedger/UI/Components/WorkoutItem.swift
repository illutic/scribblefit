import SwiftUI
import CoreModel
import CoreDesignSystem

public struct WorkoutItem: View {
    private let dateString: String
    private let exercises: [Exercise]
    private let onTapped: () -> Void

    public init(
        dateString: String,
        exercises: [Exercise],
        onTapped: @escaping () -> Void
    ) {
        self.dateString = dateString
        self.exercises = exercises
        self.onTapped = onTapped
    }

    public var body: some View {
        Button(action: onTapped) {
            VStack(alignment: .leading, spacing: 12) {
                HStack {
                    Text(dateString)
                        .font(.scribbleTitleMedium)
                        .foregroundStyle(Color.scribblePrimary)
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundStyle(Color.scribblePrimary)
                }

                VStack(alignment: .leading, spacing: 8) {
                    ForEach(exercises, id: \.id) { exercise in
                        HStack {
                            Text(exercise.canonicalName)
                                .font(.scribbleBodyMedium)
                                .foregroundStyle(Color.scribblePrimary)
                            Spacer()
                            Text(formatMetrics(for: exercise))
                                .font(.scribbleLabelMedium)
                                .foregroundStyle(Color.scribblePrimary.opacity(0.6))
                        }
                    }
                }
            }
            .padding()
            .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))
        }
    }

    private func formatMetrics(for exercise: Exercise) -> String {
        // Simple metric formatting: e.g., "3 sets" or total weight if available
        let setsCount = exercise.sets.count
        let totalWeight = exercise.sets.reduce(0.0) { $0 + Double($1.weight ?? 0.0) }
        
        if totalWeight > 0 {
            return "\(setsCount) sets, \(Int(totalWeight)) \(String(localized: "lbs"))"
        } else {
            return "\(setsCount) sets"
        }
    }
}
