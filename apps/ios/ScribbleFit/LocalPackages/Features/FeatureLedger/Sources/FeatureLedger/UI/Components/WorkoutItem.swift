import SwiftUI
import CoreModel
import CoreDesignSystem
import FeatureExercises

public struct WorkoutItem: View {
    private let dateString: String
    private let workouts: [Workout]
    private let weightUnit: WeightUnit
    private let onWorkoutTapped: (UUID) -> Void
    private let onExerciseTapped: (String) -> Void
    
    private let exerciseFormatter = FormatExerciseSummaryUseCase()

    public init(
        dateString: String,
        workouts: [Workout],
        weightUnit: WeightUnit,
        onWorkoutTapped: @escaping (UUID) -> Void,
        onExerciseTapped: @escaping (String) -> Void
    ) {
        self.dateString = dateString
        self.workouts = workouts
        self.weightUnit = weightUnit
        self.onWorkoutTapped = onWorkoutTapped
        self.onExerciseTapped = onExerciseTapped
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(dateString)
                .font(.scribbleTitleMedium)
                .foregroundStyle(Color.scribblePrimary)
                .padding(.bottom, 4)

            ForEach(workouts) { workout in
                VStack(alignment: .leading, spacing: 12) {
                    Button(action: { onWorkoutTapped(workout.id) }) {
                        HStack {
                            Text(String(localized: "Workout"))
                                .font(.scribbleLabelMedium)
                                .foregroundStyle(Color.scribbleMidGray)
                            Spacer()
                            Image(systemName: "chevron.right")
                                .font(.system(size: 12))
                                .foregroundStyle(Color.scribbleMidGray)
                        }
                    }
                    .buttonStyle(.plain)

                    VStack(alignment: .leading, spacing: 8) {
                        ForEach(workout.exercises, id: \.id) { exercise in
                            Button(action: { onExerciseTapped(exercise.canonicalName) }) {
                                HStack {
                                    Text(exercise.canonicalName)
                                        .font(.scribbleBodyMedium)
                                        .foregroundStyle(Color.scribblePrimary)
                                    Spacer()
                                    Text(exerciseFormatter.execute(exercise: exercise, weightUnit: weightUnit))
                                        .font(.scribbleLabelMedium)
                                        .foregroundStyle(Color.scribblePrimary.opacity(0.6))
                                }
                            }
                            .buttonStyle(.plain)
                        }
                    }
                }
                .padding()
                .background(Color.scribbleSurfaceContainerLow.opacity(0.4))
                .clipShape(RoundedRectangle(cornerRadius: 12))
            }
        }
        .padding()
        .background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))
    }
}
