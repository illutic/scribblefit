import SwiftUI
import CoreModel
import CoreDesignSystem

struct ScribbleConfirmationBottomSheet: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onConfirm: (Scribble) -> Void
    let onDelete: (Scribble) -> Void
    let onDismiss: () -> Void
    let onUpdateExerciseName: (UUID, String) -> Void
    let onUpdateSetWeight: (UUID, UUID, String) -> Void
    let onUpdateSetReps: (UUID, UUID, String) -> Void
    let onDeleteSet: (UUID, UUID) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 28) {
            HStack {
                Text(String(localized: "Confirm Session"))
                    .font(.system(size: 24, weight: .bold))
                    .foregroundStyle(Color.scribblePrimary)
                
                Spacer()
                
                Button(action: onDismiss) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 24))
                        .foregroundStyle(Color.scribbleMidGray.opacity(0.3))
                }
            }

            ScrollView {
                if scribble.exercises.isEmpty {
                    emptyState
                } else {
                    exercisesList
                }
            }
            .scrollDismissesKeyboard(.interactively)

            VStack(spacing: 12) {
                if scribble.status == .success {
                    confirmButton
                }
                
                deleteButton
            }
        }
        .padding(24)
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
                    onUpdateSetReps: { setId, value in onUpdateSetReps(exercise.id, setId, value) },
                    onDeleteSet: { setId in onDeleteSet(exercise.id, setId) }
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
        .accessibilityIdentifier("confirmWorkoutButton")
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
        .accessibilityIdentifier("deleteScribbleButton")
    }
}

private struct ExerciseEditCard: View {
    let exercise: Exercise
    let weightUnit: WeightUnit
    let onUpdateName: (String) -> Void
    let onUpdateSetWeight: (UUID, String) -> Void
    let onUpdateSetReps: (UUID, String) -> Void
    let onDeleteSet: (UUID) -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            TextField(String(localized: "Exercise Name"), text: Binding(
                get: { exercise.canonicalName },
                set: { onUpdateName($0) }
            ))
            .font(.system(size: 20, weight: .bold))
            .foregroundStyle(Color.scribblePrimary)
            .padding(.bottom, 4)

            VStack(spacing: 12) {
                ForEach(exercise.sets) { set in
                    SetEditRow(
                        set: set,
                        weightUnit: weightUnit,
                        onUpdateWeight: { onUpdateSetWeight(set.id, $0) },
                        onUpdateReps: { onUpdateSetReps(set.id, $0) },
                        onDelete: { onDeleteSet(set.id) }
                    )
                }
            }
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(20)
        .background(Color.scribblePrimary.opacity(0.02))
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .overlay {
            RoundedRectangle(cornerRadius: 12)
                .stroke(Color.scribblePrimary.opacity(0.05), lineWidth: 0.5)
        }
    }
}

private struct SetEditRow: View {
    let set: ExerciseSet
    let weightUnit: WeightUnit
    let onUpdateWeight: (String) -> Void
    let onUpdateReps: (String) -> Void
    let onDelete: () -> Void

    @State private var weightText: String
    @State private var repsText: String

    init(
        set: ExerciseSet,
        weightUnit: WeightUnit,
        onUpdateWeight: @escaping (String) -> Void,
        onUpdateReps: @escaping (String) -> Void,
        onDelete: @escaping () -> Void
    ) {
        self.set = set
        self.weightUnit = weightUnit
        self.onUpdateWeight = onUpdateWeight
        self.onUpdateReps = onUpdateReps
        self.onDelete = onDelete
        
        let weight = set.weight
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 1
        let formattedWeight = (weight == nil) ? "" : (formatter.string(from: NSNumber(value: weight!)) ?? "\(weight!)")
        
        _weightText = State(initialValue: formattedWeight)
        _repsText = State(initialValue: "\(set.reps)")
    }

    var body: some View {
        let weightUnitLabel = weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")

        HStack(spacing: 12) {
            Text("\(set.setNumber)")
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribbleMidGray)
                .frame(width: 24, alignment: .leading)

            HStack(spacing: 4) {
                TextField("", text: $weightText)
                    #if os(iOS)
                    .keyboardType(.decimalPad)
                    #endif
                    .font(.scribbleBodyMedium)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 50)
                    .onChange(of: weightText) { _, newValue in
                        if Float(newValue) != nil || newValue.isEmpty {
                            onUpdateWeight(newValue)
                        }
                    }
                
                Text(weightUnitLabel)
                    .font(.scribbleLabelMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 6)
            .background(Color.scribblePrimary.opacity(0.03))
            .clipShape(RoundedRectangle(cornerRadius: 6))

            Text("×")
                .font(.system(size: 14))
                .foregroundStyle(Color.scribbleMidGray)

            HStack(spacing: 4) {
                TextField("", text: $repsText)
                    #if os(iOS)
                    .keyboardType(.numberPad)
                    #endif
                    .font(.scribbleBodyMedium)
                    .fontWeight(.bold)
                    .multilineTextAlignment(.trailing)
                    .frame(width: 30)
                    .onChange(of: repsText) { _, newValue in
                        if Int(newValue) != nil || newValue.isEmpty {
                            onUpdateReps(newValue)
                        }
                    }
                
                Text(String(localized: "reps"))
                    .font(.scribbleLabelMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }
            .padding(.horizontal, 8)
            .padding(.vertical, 6)
            .background(Color.scribblePrimary.opacity(0.03))
            .clipShape(RoundedRectangle(cornerRadius: 6))

            Spacer()

            Button(action: onDelete) {
                Image(systemName: "minus.circle.fill")
                    .font(.system(size: 18))
                    .foregroundStyle(Color.scribbleDanger.opacity(0.8))
            }
            .buttonStyle(.plain)
            .accessibilityIdentifier("deleteSetButton")
        }
        .onChange(of: set.weight) { _, newValue in
            let newText = formatWeight(newValue)
            if weightText != newText {
                weightText = newText
            }
        }
        .onChange(of: set.reps) { _, newValue in
            if repsText != "\(newValue)" {
                repsText = "\(newValue)"
            }
        }
    }
    
    private func formatWeight(_ weight: Float?) -> String {
        if (weight == nil) { return "" }
        let formatter = NumberFormatter()
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 1
        return formatter.string(from: NSNumber(value: weight!)) ?? "\(weight)"
    }
}
