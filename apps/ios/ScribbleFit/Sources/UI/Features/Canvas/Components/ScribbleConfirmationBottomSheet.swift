import SwiftUI

struct ScribbleConfirmationBottomSheet: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onConfirm: (Scribble) -> Void
    let onDelete: (Scribble) -> Void
    let onDismiss: () -> Void
    let onUpdateExerciseName: (UUID, String) -> Void
    let onUpdateSetWeight: (UUID, UUID, String) -> Void
    let onUpdateSetReps: (UUID, UUID, String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 24) {
            Text(String(localized: "Confirm Exercise"))
                .font(.scribbleHeadlineSmall)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)

            ScrollView {
                if scribble.exercises.isEmpty {
                    emptyState
                } else {
                    exercisesList
                }
            }
            .scrollDismissesKeyboard(.interactively)
            .frame(maxHeight: 400)

            confirmButton
            deleteButton
        }
        .padding(.horizontal, 24)
        .padding(.top, 32)
        .padding(.bottom, 48)
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.visible)
    }

    private var emptyState: some View {
        VStack(spacing: 12) {
            Image(systemName: "exclamationmark.triangle")
                .font(.system(size: 48))
                .foregroundStyle(Color.scribbleMidGray)
            Text(String(localized: "No exercises parsed from your scribble."))
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)
        }
        .padding(.top, 40)
        .frame(maxWidth: .infinity)
    }

    private var exercisesList: some View {
        VStack(spacing: 16) {
            ForEach(scribble.exercises) { exercise in
                ExerciseEditCard(
                    exercise: exercise,
                    weightUnit: weightUnit,
                    onUpdateName: { onUpdateExerciseName(exercise.id, $0) },
                    onUpdateSetWeight: { setId, value in onUpdateSetWeight(exercise.id, setId, value) },
                    onUpdateSetReps: { setId, value in onUpdateSetReps(exercise.id, setId, value) }
                )
            }
        }
    }

    private var confirmButton: some View {
        Button(action: { onConfirm(scribble) }) {
            Text(String(localized: "Confirm Workout"))
                .font(.scribbleTitleMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribbleOnPrimary)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(Color.scribblePrimary)
                .clipShape(Capsule())
        }
    }

    private var deleteButton: some View {
        Button(action: { onDelete(scribble) }) {
            Text(String(localized: "Delete"))
                .font(.scribbleBodyMedium)
                .fontWeight(.semibold)
                .foregroundStyle(Color.scribbleDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(Color.scribbleDanger.opacity(0.1))
                .clipShape(Capsule())
        }
    }
}

private struct ExerciseEditCard: View {
    let exercise: Exercise
    let weightUnit: WeightUnit
    let onUpdateName: (String) -> Void
    let onUpdateSetWeight: (UUID, String) -> Void
    let onUpdateSetReps: (UUID, String) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            TextField(String(localized: "Exercise Name"), text: Binding(
                get: { exercise.canonicalName },
                set: { onUpdateName($0) }
            ))
            .font(.scribbleTitleMedium)
            .fontWeight(.bold)
            .foregroundStyle(Color.scribblePrimary)

            ForEach(exercise.sets) { set in
                SetEditRow(
                    set: set,
                    weightUnit: weightUnit,
                    onUpdateWeight: { onUpdateSetWeight(set.id, $0) },
                    onUpdateReps: { onUpdateSetReps(set.id, $0) }
                )
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(16)
        .background(Color.scribbleSurfaceContainerLow)
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}

private struct SetEditRow: View {
    let set: ExerciseSet
    let weightUnit: WeightUnit
    let onUpdateWeight: (String) -> Void
    let onUpdateReps: (String) -> Void

    @State private var weightText: String = ""
    @State private var repsText: String = ""

    var body: some View {
        let weightUnitLabel = weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")

        HStack(spacing: 8) {
            Text(String(localized: "Set \(set.setNumber):"))
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)

            TextField("", text: $weightText)
                #if os(iOS)
                .keyboardType(.decimalPad)
                #endif
                .frame(width: 60)
                .font(.scribbleBodyMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)
                .padding(4)
                .background(Color.scribbleSurfaceContainerHigh.opacity(0.3))
                .clipShape(RoundedRectangle(cornerRadius: 4))
                .onChange(of: weightText) { _, newValue in
                    if Float(newValue) != nil {
                        onUpdateWeight(newValue)
                    }
                }

            Text(weightUnitLabel)
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)

            Text("x")
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)

            TextField("", text: $repsText)
                #if os(iOS)
                .keyboardType(.numberPad)
                #endif
                .frame(width: 40)
                .font(.scribbleBodyMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)
                .padding(4)
                .background(Color.scribbleSurfaceContainerHigh.opacity(0.3))
                .clipShape(RoundedRectangle(cornerRadius: 4))
                .onChange(of: repsText) { _, newValue in
                    if Int(newValue) != nil {
                        onUpdateReps(newValue)
                    }
                }

            Text(String(localized: "reps"))
                .font(.scribbleBodyMedium)
                .foregroundStyle(Color.scribbleMidGray)

            Spacer()
        }
        .onAppear {
            weightText = String(format: "%.1f", set.weight)
            repsText = "\(set.reps)"
        }
    }
}
