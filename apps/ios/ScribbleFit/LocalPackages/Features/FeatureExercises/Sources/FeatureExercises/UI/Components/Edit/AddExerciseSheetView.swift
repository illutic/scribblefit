import SwiftUI
import CoreModel
import CoreDesignSystem

public struct AddExerciseSheetView: View {
    public let weightUnitLabel: String
    public let onDismiss: () -> Void
    public let onSave: (String, String, [ExerciseSet], String) -> Void
    
    public init(
        weightUnitLabel: String,
        onDismiss: @escaping () -> Void,
        onSave: @escaping (String, String, [ExerciseSet], String) -> Void
    ) {
        self.weightUnitLabel = weightUnitLabel
        self.onDismiss = onDismiss
        self.onSave = onSave
    }
    
    @State private var exerciseName = ""
    @State private var muscleGroup = ""
    @State private var notes = ""
    @State private var sets: [ExerciseSet] = [
        ExerciseSet(id: UUID(), setNumber: 1, weight: 0.0, reps: 0),
        ExerciseSet(id: UUID(), setNumber: 2, weight: 0.0, reps: 0)
    ]
    
    var isSaveEnabled: Bool {
        !exerciseName.trimmingCharacters(in: .whitespaces).isEmpty &&
        !muscleGroup.trimmingCharacters(in: .whitespaces).isEmpty &&
        sets.contains { ($0.weight ?? 0.0) > 0 && $0.reps > 0 }
    }
    
    public var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("EXERCISE")
                            .font(.scribbleLabelSmall.bold())
                            .foregroundColor(.scribbleMidGray)
                        
                        TextField("e.g. Bench Press", text: $exerciseName)
                            .padding(16)
                            .background(Color.scribbleSurfaceContainerLow)
                            .cornerRadius(12)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("MUSCLE GROUP")
                            .font(.scribbleLabelSmall.bold())
                            .foregroundColor(.scribbleMidGray)
                        
                        TextField("e.g. Chest", text: $muscleGroup)
                            .padding(16)
                            .background(Color.scribbleSurfaceContainerLow)
                            .cornerRadius(12)
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("SETS")
                            .font(.scribbleLabelSmall.bold())
                            .foregroundColor(.scribbleMidGray)
                        
                        ForEach(sets.indices, id: \.self) { index in
                            HStack(spacing: 12) {
                                Text("\(sets[index].setNumber)")
                                    .font(.scribbleBodyMedium)
                                    .foregroundColor(.scribbleMidGray)
                                    .frame(width: 20)
                                
                                HStack {
                                    TextField("0", value: weightBinding(for: index), formatter: NumberFormatter.decimal)
                                        #if os(iOS)
                                        .keyboardType(.decimalPad)
                                        #endif
                                        .multilineTextAlignment(.trailing)
                                    Text(weightUnitLabel)
                                }
                                .padding(8)
                                .background(Color.scribbleSurfaceContainerLow)
                                .cornerRadius(8)
                                
                                Text("x")
                                    .foregroundColor(.scribbleMidGray)
                                
                                HStack {
                                    TextField("0", value: repsBinding(for: index), formatter: NumberFormatter.integer)
                                        #if os(iOS)
                                        .keyboardType(.numberPad)
                                        #endif
                                        .multilineTextAlignment(.trailing)
                                    Text("r")
                                }
                                .padding(8)
                                .background(Color.scribbleSurfaceContainerLow)
                                .cornerRadius(8)
                                
                                if sets.count > 1 {
                                    Button(action: {
                                        sets.remove(at: index)
                                    }) {
                                        Image(systemName: "xmark")
                                            .foregroundColor(.scribbleDanger.opacity(0.6))
                                    }
                                } else {
                                    Spacer().frame(width: 24)
                                }
                            }
                        }
                        
                        Button(action: {
                            let nextNum = (sets.map { $0.setNumber }.max() ?? 0) + 1
                            sets.append(ExerciseSet(id: UUID(), setNumber: nextNum, weight: 0.0, reps: 0))
                        }) {
                            HStack {
                                Image(systemName: "plus.circle.fill")
                                Text("ADD SET")
                            }
                            .font(.scribbleLabelMedium.bold())
                            .foregroundColor(.scribblePrimary)
                            .padding(.vertical, 8)
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("NOTES")
                            .font(.scribbleLabelSmall.bold())
                            .foregroundColor(.scribbleMidGray)
                        
                        TextEditor(text: $notes)
                            .frame(minHeight: 80)
                            .padding(8)
                            .background(Color.scribbleSurfaceContainerLow)
                            .cornerRadius(12)
                    }
                }
                .padding(24)
            }
            .navigationTitle("New Entry")
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar {
                #if os(iOS)
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Close") { onDismiss() }
                        .foregroundColor(.scribblePrimary)
                }
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("SAVE") { onSave(exerciseName, muscleGroup, sets, notes) }
                        .foregroundColor(isSaveEnabled ? .scribblePrimary : .scribbleMidGray)
                        .disabled(!isSaveEnabled)
                }
                #else
                ToolbarItem {
                    Button("Close") { onDismiss() }
                }
                ToolbarItem {
                    Button("SAVE") { onSave(exerciseName, muscleGroup, sets, notes) }
                        .disabled(!isSaveEnabled)
                }
                #endif
            }
        }
    }

    private func weightBinding(for index: Int) -> Binding<Float> {
        Binding(
            get: { sets[index].weight ?? 0.0 },
            set: { sets[index].weight = $0 }
        )
    }

    private func repsBinding(for index: Int) -> Binding<Int> {
        Binding(
            get: { sets[index].reps },
            set: { sets[index].reps = $0 }
        )
    }
}

extension NumberFormatter {
    static var decimal: NumberFormatter {
        let f = NumberFormatter()
        f.numberStyle = .decimal
        return f
    }
    static var integer: NumberFormatter {
        let f = NumberFormatter()
        f.numberStyle = .none
        return f
    }
}
