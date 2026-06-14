import SwiftUI
import CoreModel
import CoreDesignSystem

import FeatureExercises

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
    let onDeleteExercise: (UUID) -> Void
    let onAddSet: (UUID) -> Void

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
                ExerciseEditItem(
                    exercise: exercise,
                    weightUnitLabel: weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs"),
                    setRepsSeparator: "x",
                    repsLabel: "reps",
                    addSetLabel: "Add Set",
                    onUpdateName: { id, name in onUpdateExerciseName(id, name) },
                    onDeleteExercise: { id in onDeleteExercise(id) },
                    onUpdateSetWeight: { exId, setId, value in onUpdateSetWeight(exId, setId, value) },
                    onUpdateSetReps: { exId, setId, value in onUpdateSetReps(exId, setId, value) },
                    onDeleteSet: { exId, setId in onDeleteSet(exId, setId) },
                    onAddSet: { id in onAddSet(id) }
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
