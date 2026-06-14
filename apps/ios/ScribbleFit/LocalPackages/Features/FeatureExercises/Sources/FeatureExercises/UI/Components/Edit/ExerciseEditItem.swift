import SwiftUI
import CoreModel
import CoreDesignSystem

public struct ExerciseEditItem: View {
    public let exercise: Exercise
    public let weightUnitLabel: String
    public let setRepsSeparator: String
    public let repsLabel: String
    public let addSetLabel: String
    public let onUpdateName: (UUID, String) -> Void
    public let onDeleteExercise: (UUID) -> Void
    public let onUpdateSetWeight: (UUID, UUID, String) -> Void
    public let onUpdateSetReps: (UUID, UUID, String) -> Void
    public let onDeleteSet: (UUID, UUID) -> Void
    public let onAddSet: (UUID) -> Void

    @State private var nameText: String

    public init(
        exercise: Exercise,
        weightUnitLabel: String,
        setRepsSeparator: String,
        repsLabel: String,
        addSetLabel: String,
        onUpdateName: @escaping (UUID, String) -> Void,
        onDeleteExercise: @escaping (UUID) -> Void,
        onUpdateSetWeight: @escaping (UUID, UUID, String) -> Void,
        onUpdateSetReps: @escaping (UUID, UUID, String) -> Void,
        onDeleteSet: @escaping (UUID, UUID) -> Void,
        onAddSet: @escaping (UUID) -> Void
    ) {
        self.exercise = exercise
        self.weightUnitLabel = weightUnitLabel
        self.setRepsSeparator = setRepsSeparator
        self.repsLabel = repsLabel
        self.addSetLabel = addSetLabel
        self.onUpdateName = onUpdateName
        self.onDeleteExercise = onDeleteExercise
        self.onUpdateSetWeight = onUpdateSetWeight
        self.onUpdateSetReps = onUpdateSetReps
        self.onDeleteSet = onDeleteSet
        self.onAddSet = onAddSet
        _nameText = State(initialValue: exercise.canonicalName)
    }

    public var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack {
                TextField("", text: $nameText)
                    .textFieldStyle(.plain)
                    .font(.scribbleTitleMedium.bold())
                    .foregroundColor(.scribblePrimary)
                    .onChange(of: nameText) { _, newValue in
                        onUpdateName(exercise.id, newValue)
                    }

                Spacer()

                Button(action: { onDeleteExercise(exercise.id) }) {
                    Image(systemName: "trash")
                        .font(.system(size: 16))
                        .foregroundColor(.scribbleMidGray.opacity(0.5))
                }
            }

            VStack(spacing: 8) {
                ForEach(exercise.sets) { set in
                    SetEditRow(
                        set: set,
                        weightUnitLabel: weightUnitLabel,
                        setRepsSeparator: setRepsSeparator,
                        repsLabel: repsLabel,
                        onUpdateWeight: { weight in onUpdateSetWeight(exercise.id, set.id, weight) },
                        onUpdateReps: { reps in onUpdateSetReps(exercise.id, set.id, reps) },
                        onDelete: { onDeleteSet(exercise.id, set.id) }
                    )
                }
            }

            Button(action: { onAddSet(exercise.id) }) {
                HStack(spacing: 8) {
                    Image(systemName: "plus.circle.fill")
                        .font(.system(size: 16))
                    Text(addSetLabel.uppercased())
                        .font(.scribbleLabelMedium.bold())
                }
                .foregroundColor(.scribblePrimary)
                .padding(.vertical, 8)
            }
        }
        .padding(16)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.scribbleSurfaceContainerLow)
        )
    }
}
