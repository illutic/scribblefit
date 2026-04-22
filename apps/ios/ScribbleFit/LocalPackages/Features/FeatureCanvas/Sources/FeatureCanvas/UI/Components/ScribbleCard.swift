import SwiftUI
import CoreModel
import CoreDesignSystem

struct ScribbleCard: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onClick: () -> Void
    let onExerciseClick: (String) -> Void
    let onWorkoutExercisesClick: (UUID) -> Void
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        switch scribble.status {
        case .pending, .parsing:
            PendingScribbleCard(scribble: scribble)
        case .success:
            ParsedScribbleCard(scribble: scribble, weightUnit: weightUnit, onClick: onClick)
        case .completed:
            LoggedScribbleCard(
                scribble: scribble,
                weightUnit: weightUnit,
                onClick: {
                    if let workoutId = scribble.workoutId {
                        onWorkoutExercisesClick(workoutId)
                    } else {
                        onClick()
                    }
                },
                onExerciseClick: onExerciseClick
            )
        case .failed:
            FailedScribbleCard(scribble: scribble, onIntent: onIntent)
        }
    }
}

private struct PendingScribbleCard: View {
    let scribble: Scribble
    @State private var isAnimating = false
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            ScribbleRawText(text: scribble.rawText)
            
            HStack(spacing: 8) {
                ProgressView()
                    .tint(Color.scribblePrimary)
                    .scaleEffect(0.8)
                
                Text(String(localized: "Parsing workout data…"))
                    .font(.scribbleLabelMedium)
                    .foregroundStyle(Color.scribbleMidGray)
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .scribbleGlass(cornerRadius: 16)
        .opacity(isAnimating ? 0.6 : 1.0)
        .onAppear {
            withAnimation(.easeInOut(duration: 1.0).repeatForever(autoreverses: true)) {
                isAnimating = true
            }
        }
    }
}

private struct ParsedScribbleCard: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onClick: () -> Void
    
    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 20) {
                HStack(alignment: .top) {
                    VStack(alignment: .leading, spacing: 8) {
                        ScribbleRawText(text: scribble.rawText)
                        
                        if scribble.exercises.isEmpty {
                            Text(String(localized: "No exercises detected"))
                                .font(.scribbleLabelMedium)
                                .foregroundStyle(Color.scribbleMidGray.opacity(0.6))
                                .padding(.top, 4)
                        } else {
                            ForEach(scribble.exercises) { exercise in
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(exercise.canonicalName)
                                        .font(.system(size: 24, weight: .bold))
                                        .kerning(-0.5)
                                        .foregroundStyle(Color.scribblePrimary)
                                    
                                    Text(exercise.summary(weightUnit: weightUnit))
                                        .font(.scribbleBodyMedium)
                                        .foregroundStyle(Color.scribbleMidGray)
                                }
                                .padding(.top, 4)
                            }
                        }
                    }
                    
                    Spacer()
                    
                    Circle()
                        .fill(Color.scribblePrimary)
                        .frame(width: 36, height: 36)
                        .overlay {
                            Image(systemName: "checkmark.seal.fill")
                                .font(.system(size: 18))
                                .foregroundStyle(Color.scribbleOnPrimary)
                        }
                }
                
                Text(String(localized: "TAP TO CONFIRM").uppercased())
                    .font(.scribbleLabelMedium)
                    .fontWeight(.bold)
                    .kerning(1)
                    .padding(.horizontal, 12)
                    .padding(.vertical, 8)
                    .background(Color.scribblePrimary.opacity(0.05))
                    .clipShape(RoundedRectangle(cornerRadius: 8))
                    .foregroundStyle(Color.scribblePrimary)
            }
            .padding(20)
            .scribbleGlass(cornerRadius: 16)
            .overlay {
                RoundedRectangle(cornerRadius: 16)
                    .stroke(Color.scribblePrimary.opacity(0.05), lineWidth: 0.5)
            }
        }
        .buttonStyle(.plain)
    }
}

private struct LoggedScribbleCard: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onClick: () -> Void
    let onExerciseClick: (String) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 20) {
            if scribble.exercises.isEmpty {
                Button(action: onClick) {
                    VStack(alignment: .leading, spacing: 8) {
                        ScribbleRawText(text: scribble.rawText)
                        
                        Text(String(localized: "LOGGED (NO EXERCISES)"))
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                            .foregroundStyle(Color.scribblePrimary.opacity(0.4))
                    }
                }
                .buttonStyle(.plain)
            } else {
                ForEach(scribble.exercises) { exercise in
                    Button(action: { onExerciseClick(exercise.canonicalName) }) {
                        VStack(alignment: .leading, spacing: 20) {
                            HStack(alignment: .center) {
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(exercise.canonicalName)
                                        .font(.system(size: 28, weight: .bold))
                                        .kerning(-1)
                                        .foregroundStyle(Color.scribblePrimary)
                                    
                                    Text(exercise.summary(weightUnit: weightUnit))
                                        .font(.scribbleBodyMedium)
                                        .foregroundStyle(Color.scribbleMidGray)
                                }
                                
                                Spacer()
                                
                                if exercise == scribble.exercises.first {
                                    HStack(spacing: 4) {
                                        Image(systemName: "checkmark.circle.fill")
                                            .font(.system(size: 14))
                                        Text(String(localized: "LOGGED").uppercased())
                                            .font(.scribbleLabelMedium)
                                            .fontWeight(.bold)
                                            .kerning(1)
                                    }
                                    .padding(.horizontal, 10)
                                    .padding(.vertical, 6)
                                    .background(Color.scribblePrimary.opacity(0.05))
                                    .clipShape(Capsule())
                                    .foregroundStyle(Color.scribblePrimary)
                                }
                            }
                            
                            if exercise.estimated1RM != nil || exercise.intensity != nil {
                                HStack(spacing: 12) {
                                    if let oneRm = exercise.estimated1RM {
                                        StatCardView(
                                            label: String(localized: "EST. 1RM"),
                                            value: "\(Int(oneRm))\(weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs"))"
                                        )
                                    }
                                    if let intensity = exercise.intensity {
                                        StatCardView(
                                            label: String(localized: "INTENSITY"),
                                            value: "\(Int(intensity * 100))%"
                                        )
                                    }
                                }
                            }
                            
                            if exercise != scribble.exercises.last {
                                Divider()
                                    .background(Color.scribblePrimary.opacity(0.1))
                            }
                        }
                    }
                    .buttonStyle(.plain)
                }
            }
        }
        .padding(20)
        .scribbleGlass(cornerRadius: 16)
        .opacity(0.8)
    }
}

private struct FailedScribbleCard: View {
    let scribble: Scribble
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            ScribbleRawText(text: scribble.rawText, color: Color.red.opacity(0.7))
            
            HStack(spacing: 12) {
                Button(action: { onIntent(.retryScribbleParsing(scribble)) }) {
                    Text(String(localized: "Retry"))
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .foregroundStyle(Color.white)
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Color.scribblePrimary)
                        .clipShape(Capsule())
                }
                
                Button(action: { onIntent(.deleteScribble(scribble.id)) }) {
                    Text(String(localized: "Remove"))
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .foregroundStyle(Color.scribbleMidGray)
                }
            }
        }
        .padding(20)
        .frame(maxWidth: .infinity, alignment: .leading)
        .background(Color.red.opacity(0.05))
        .clipShape(RoundedRectangle(cornerRadius: 16))
        .overlay {
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.red.opacity(0.1), lineWidth: 1)
        }
    }
}

private struct ScribbleRawText: View {
    let text: String
    var color: Color = Color.scribbleMidGray
    
    var body: some View {
        Text("\"\(text)\"")
            .font(.scribbleBodyMedium)
            .italic()
            .foregroundStyle(color)
    }
}
