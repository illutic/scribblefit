import SwiftUI
#if SWIFT_PACKAGE
import CoreModel
import CoreDesignSystem
#endif

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
                        .foregroundStyle(.scribblePrimary)
                    Spacer()
                    Image(systemName: "chevron.right")
                        .foregroundStyle(.scribblePrimary)
                }

                VStack(alignment: .leading, spacing: 8) {
                    ForEach(exercises) { exercise in
                        HStack {
                            Text(exercise.name)
                                .font(.scribbleBodyMedium)
                                .foregroundStyle(.scribblePrimary)
                            Spacer()
                            Text(formatMetrics(for: exercise))
                                .font(.scribbleLabelMedium)
                                .foregroundStyle(.scribblePrimary.opacity(0.6))
                        }
                    }
                }
            }
            .padding()
            .scribbleGlass(cornerRadius: 16)
        }
    }

    private func formatMetrics(for exercise: Exercise) -> String {
        // Simple metric formatting: e.g., "3 sets" or total weight if available
        let setsCount = exercise.sets.count
        let totalWeight = exercise.sets.reduce(0.0) { $0 + ($1.weight ?? 0.0) }
        
        if totalWeight > 0 {
            return "\(setsCount) sets, \(Int(totalWeight)) \(String(localized: "lbs"))"
        } else {
            return "\(setsCount) sets"
        }
    }
}
