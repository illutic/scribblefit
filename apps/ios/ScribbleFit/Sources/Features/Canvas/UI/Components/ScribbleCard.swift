import SwiftUI
import CoreModel
import CoreDesignSystem

struct ScribbleCard: View {
    let scribble: Scribble
    let weightUnit: WeightUnit
    let onClick: () -> Void
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        Group {
            switch scribble.status {
            case .pending, .parsing:
                PendingScribbleCard(scribble: scribble)
            case .success:
                ParsedScribbleCard(scribble: scribble, weightUnit: weightUnit, onClick: onClick)
            case .completed:
                LoggedScribbleCard(scribble: scribble, weightUnit: weightUnit, onClick: onClick)
            case .failed:
                FailedScribbleCard(scribble: scribble, onIntent: onIntent)
            }
        }
    }
}

private struct PendingScribbleCard: View {
    let scribble: Scribble
    @State private var rotation: Double = 0
    @State private var progress: CGFloat = 0

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .top) {
                Text("\"\(scribble.rawText)\"")
                    .font(.scribbleTitleMedium)
                    .italic()
                    .foregroundStyle(Color.scribbleMidGray)
                
                Spacer()
                
                Image(systemName: "arrow.triangle.2.circlepath")
                    .font(.system(size: 20))
                    .foregroundStyle(Color.scribblePrimary)
                    .rotationEffect(.degrees(rotation))
            }
            
            ZStack(alignment: .leading) {
                Capsule()
                    .fill(Color.scribblePrimary.opacity(0.1))
                    .frame(height: 2)
                
                Capsule()
                    .fill(Color.scribblePrimary)
                    .frame(width: 200 * progress, height: 2)
            }
            
            Text(String(localized: "PARSING WORKOUT DATA").uppercased())
                .font(.scribbleLabelMedium)
                .foregroundStyle(Color.scribblePrimary.opacity(0.4))
                .fontWeight(.bold)
                .kerning(1)
        }
        .padding(20)
        .scribbleGlass(cornerRadius: 16)
        .onAppear {
            withAnimation(.linear(duration: 1).repeatForever(autoreverses: false)) {
                rotation = 360
            }
            withAnimation(.linear(duration: 2).repeatForever(autoreverses: false)) {
                progress = 1.0
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
                        Text("\"\(scribble.rawText)\"")
                            .font(.scribbleBodyMedium)
                            .italic()
                            .foregroundStyle(Color.scribbleMidGray)
                        
                        if let exercise = scribble.exercises.first {
                            Text(exercise.canonicalName)
                                .font(.system(size: 28, weight: .bold))
                                .kerning(-1)
                                .foregroundStyle(Color.scribblePrimary)
                            
                            Text(exercise.summary(weightUnit: weightUnit))
                                .font(.scribbleBodyMedium)
                                .foregroundStyle(Color.scribbleMidGray)
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
    
    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 20) {
                ForEach(scribble.exercises) { exercise in
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
                                    StatCard(
                                        label: String(localized: "EST. 1RM"),
                                        value: "\(Int(oneRm))\(weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs"))"
                                    )
                                }
                                if let intensity = exercise.intensity {
                                    StatCard(
                                        label: String(localized: "INTENSITY"),
                                        value: "\(Int(intensity * 100))%"
                                    )
                                }
                            }
                        }
                        
                        if let improvement = exercise.improvement {
                            let sign = improvement >= 0 ? "+" : ""
                            let unit = weightUnit == .kgs ? String(localized: "kg") : String(localized: "lbs")
                            HStack(spacing: 4) {
                                Image(systemName: "clock.arrow.circlepath")
                                    .font(.system(size: 12))
                                Text(String(localized: "\(sign)\(Int(improvement))\(unit) VS LAST SESSION").uppercased())
                                    .font(.scribbleLabelMedium)
                                    .fontWeight(.semibold)
                                    .kerning(0.5)
                            }
                            .foregroundStyle(Color.scribbleMidGray)
                        }
                        
                        if exercise != scribble.exercises.last {
                            Divider()
                                .background(Color.scribblePrimary.opacity(0.1))
                        }
                    }
                }
            }
            .padding(20)
            .scribbleGlass(cornerRadius: 16)
            .opacity(0.8)
        }
        .buttonStyle(.plain)
    }
}

private struct FailedScribbleCard: View {
    let scribble: Scribble
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            HStack(alignment: .top) {
                VStack(alignment: .leading, spacing: 8) {
                    Text("\"\(scribble.rawText)\"")
                        .font(.scribbleTitleMedium)
                        .italic()
                        .foregroundStyle(Color.scribbleMidGray)
                    
                    HStack(spacing: 6) {
                        Image(systemName: "exclamationmark.triangle.fill")
                            .font(.system(size: 14))
                        Text(String(localized: "FAILED TO PARSE WORKOUT").uppercased())
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                            .kerning(1)
                    }
                    .foregroundStyle(Color.scribbleDanger)
                }
                
                Spacer()
                
                Circle()
                    .fill(Color.scribbleDanger.opacity(0.1))
                    .frame(width: 36, height: 36)
                    .overlay {
                        Image(systemName: "exclamationmark")
                            .foregroundStyle(Color.scribbleDanger)
                            .fontWeight(.bold)
                    }
            }
            
            HStack(spacing: 16) {
                Button(action: { onIntent(.retryScribbleParsing(scribble)) }) {
                    Text(String(localized: "RETRY").uppercased())
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .kerning(1)
                        .foregroundStyle(Color.scribblePrimary)
                }
                
                Button(action: { onIntent(.deleteScribble(scribble.id)) }) {
                    Text(String(localized: "REMOVE").uppercased())
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .kerning(1)
                        .foregroundStyle(Color.scribbleMidGray)
                }
            }
            .padding(.top, 8)
        }
        .padding(20)
        .scribbleGlass(cornerRadius: 16)
        .overlay {
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.scribbleDanger.opacity(0.05), lineWidth: 0.5)
        }
    }
}

private struct StatCard: View {
    let label: String
    let value: String
    
    var body: some View {
        VStack(alignment: .leading, spacing: 4) {
            Text(label)
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)
            
            Text(value)
                .font(.scribbleTitleMedium)
                .fontWeight(.bold)
                .foregroundStyle(Color.scribblePrimary)
        }
        .frame(maxWidth: .infinity, alignment: .leading)
        .padding(12)
        .background(Color.scribblePrimary.opacity(0.03))
        .clipShape(RoundedRectangle(cornerRadius: 12))
    }
}
