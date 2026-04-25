import SwiftUI
import CoreModel
import CoreDesignSystem

public struct SetEditRow: View {
    public let set: ExerciseSet
    public let weightUnitLabel: String
    public let setRepsSeparator: String
    public let repsLabel: String
    public let onUpdateWeight: (String) -> Void
    public let onUpdateReps: (String) -> Void
    public let onDelete: () -> Void
    
    @State private var weightText: String
    @State private var repsText: String
    
    public init(
        set: ExerciseSet,
        weightUnitLabel: String,
        setRepsSeparator: String,
        repsLabel: String,
        onUpdateWeight: @escaping (String) -> Void,
        onUpdateReps: @escaping (String) -> Void,
        onDelete: @escaping () -> Void
    ) {
        self.set = set
        self.weightUnitLabel = weightUnitLabel
        self.setRepsSeparator = setRepsSeparator
        self.repsLabel = repsLabel
        self.onUpdateWeight = onUpdateWeight
        self.onUpdateReps = onUpdateReps
        self.onDelete = onDelete
        
        let initialWeight = set.weight
        if let weight = initialWeight {
            _weightText = State(initialValue: weight == 0.0 ? "" : String(format: "%.1f", weight))
        } else {
            _weightText = State(initialValue: "")
        }
        
        _repsText = State(initialValue: set.reps == 0 ? "" : "\(set.reps)")
    }
    
    public var body: some View {
        HStack(spacing: 12) {
            Text("\(set.setNumber).")
                .font(.scribbleBodyMedium)
                .foregroundColor(.scribbleMidGray)
                .frame(width: 24, alignment: .leading)
            
            TextField("", text: $weightText)
                .keyboardType(.decimalPad)
                .textFieldStyle(.plain)
                .font(.scribbleBodyMedium.bold())
                .foregroundColor(.scribblePrimary)
                .frame(width: 60)
                .onChange(of: weightText) { oldValue, newValue in
                    if newValue.isEmpty || newValue.range(of: "^\\d*\\.?\\d*$", options: .regularExpression) != nil {
                        onUpdateWeight(newValue)
                    }
                }
            
            Text(weightUnitLabel)
                .font(.scribbleBodyMedium)
                .foregroundColor(.scribbleMidGray)
            
            Text(setRepsSeparator)
                .font(.scribbleBodyMedium)
                .foregroundColor(.scribbleMidGray)
            
            TextField("", text: $repsText)
                .keyboardType(.numberPad)
                .textFieldStyle(.plain)
                .font(.scribbleBodyMedium)
                .foregroundColor(.scribblePrimary)
                .frame(width: 40)
                .onChange(of: repsText) { oldValue, newValue in
                    if newValue.isEmpty || newValue.allSatisfy({ $0.isNumber }) {
                        onUpdateReps(newValue)
                    }
                }
            
            Text(repsLabel)
                .font(.scribbleBodyMedium)
                .foregroundColor(.scribbleMidGray)
            
            Spacer()
            
            Button(action: onDelete) {
                Image(systemName: "xmark")
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(.scribbleDanger.opacity(0.8))
                    .padding(8)
            }
        }
        .padding(.vertical, 4)
    }
}
