import SwiftUI

struct ScribbleConfirmationBottomSheet: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onConfirm: (Scribble) -> Void
    let onEdit: (Scribble) -> Void
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
                } else {
                    VStack(spacing: 16) {
                        ForEach(scribble.exercises) { exercise in
                            VStack(alignment: .leading, spacing: 12) {
                                TextField(String(localized: "Exercise Name"), text: Binding(
                                    get: { exercise.canonicalName },
                                    set: { onUpdateExerciseName(exercise.id, $0) }
                                ))
                                .font(.scribbleTitleMedium)
                                .fontWeight(.bold)
                                .foregroundStyle(Color.scribblePrimary)
                                
                                ForEach(exercise.sets) { set in
                                    let weightUnitLabel = weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")
                                    
                                    HStack(spacing: 8) {
                                        Text(String(localized: "Set \(set.setNumber):"))
                                            .font(.scribbleBodyMedium)
                                            .foregroundStyle(Color.scribbleMidGray)
                                        
                                        TextField("", text: Binding(
                                            get: { String(format: "%.1f", set.weight) },
                                            set: { onUpdateSetWeight(exercise.id, set.id, $0) }
                                        ))
                                        .keyboardType(.decimalPad)
                                        .frame(width: 60)
                                        .font(.scribbleBodyMedium)
                                        .fontWeight(.bold)
                                        .foregroundStyle(Color.scribblePrimary)
                                        .padding(4)
                                        .background(Color.scribbleSurfaceContainerHigh.opacity(0.3))
                                        .clipShape(RoundedRectangle(cornerRadius: 4))
                                        
                                        Text(weightUnitLabel)
                                            .font(.scribbleBodyMedium)
                                            .foregroundStyle(Color.scribbleMidGray)
                                        
                                        Text("x")
                                            .font(.scribbleBodyMedium)
                                            .foregroundStyle(Color.scribbleMidGray)
                                        
                                        TextField("", text: Binding(
                                            get: { "\(set.reps)" },
                                            set: { onUpdateSetReps(exercise.id, set.id, $0) }
                                        ))
                                        .keyboardType(.numberPad)
                                        .frame(width: 40)
                                        .font(.scribbleBodyMedium)
                                        .fontWeight(.bold)
                                        .foregroundStyle(Color.scribblePrimary)
                                        .padding(4)
                                        .background(Color.scribbleSurfaceContainerHigh.opacity(0.3))
                                        .clipShape(RoundedRectangle(cornerRadius: 4))
                                        
                                        Text(String(localized: "reps"))
                                            .font(.scribbleBodyMedium)
                                            .foregroundStyle(Color.scribbleMidGray)
                                        
                                        Spacer()
                                    }
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .padding(16)
                            .background(Color.scribbleSurfaceContainerLow)
                            .clipShape(RoundedRectangle(cornerRadius: 12))
                        }
                    }
                }
            }
            .scrollDismissesKeyboard(.interactively)
            .frame(maxHeight: 400)

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

            HStack(spacing: 16) {
                Button(action: { onEdit(scribble) }) {
                    Text(String(localized: "Parse Again"))
                        .font(.scribbleBodyMedium)
                        .fontWeight(.semibold)
                        .foregroundStyle(Color.scribblePrimary)
                        .frame(maxWidth: .infinity)
                        .frame(height: 56)
                        .overlay {
                            Capsule()
                                .stroke(Color.scribbleSurfaceContainerHigh, lineWidth: 1)
                        }
                }

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
        .padding(.horizontal, 24)
        .padding(.top, 32)
        .padding(.bottom, 48)
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.visible)
    }
}
