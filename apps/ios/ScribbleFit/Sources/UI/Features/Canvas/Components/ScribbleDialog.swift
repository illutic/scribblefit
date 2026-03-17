import SwiftUI

struct ScribbleDialog: View {
    let scribble: Scribble
    let onConfirm: () -> Void
    let onDismiss: () -> Void
    let onDelete: () -> Void
    let onEdit: () -> Void
    @Environment(\.scribbleFitColors) var colors

    var body: some View {
        VStack(spacing: 0) {
            Spacer()
            
            VStack(alignment: .leading, spacing: 0) {
                // Handle
                HStack {
                    Spacer()
                    Capsule()
                        .fill(colors.lightGray)
                        .frame(width: 36, height: 5)
                    Spacer()
                }
                .padding(.top, 8)
                .padding(.bottom, 16)

                VStack(alignment: .leading, spacing: 8) {
                    Text("Confirm Workout")
                        .font(.system(size: 24, weight: .bold))
                        .foregroundColor(colors.richBlack)
                    
                    Text(scribble.rawText)
                        .font(.system(size: 14))
                        .foregroundColor(colors.strongGray)
                }
                .padding(.horizontal, 24)
                
                ScrollView {
                    VStack(alignment: .leading, spacing: 16) {
                        ForEach(scribble.exercises) { exercise in
                            VStack(alignment: .leading, spacing: 4) {
                                Text(exercise.name)
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(colors.richBlack)
                                
                                ForEach(exercise.sets) { set in
                                    Text("\(set.reps) x \(String(format: "%.1f", set.weight)) kg")
                                        .font(.system(size: 14))
                                        .foregroundColor(colors.richBlack)
                                }
                            }
                        }
                    }
                    .padding(.horizontal, 24)
                    .padding(.vertical, 16)
                }
                .frame(maxHeight: 300)
                
                HStack(spacing: 8) {
                    Button(action: onDelete) {
                        Text("Delete")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(colors.dangerRed)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                    }
                    .glassEffect(.regular.interactive(), in: .capsule)
                    
                    Button(action: onEdit) {
                        Text("Edit")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(colors.richBlack)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                    }
                    .glassEffect(.regular.interactive(), in: .capsule)
                    
                    Button(action: onConfirm) {
                        Text("Confirm")
                            .font(.system(size: 14, weight: .medium))
                            .foregroundColor(colors.background)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 12)
                            .background(colors.richBlack)
                            .cornerRadius(100)
                    }
                }
                .padding(.horizontal, 24)
                .padding(.bottom, 32)
            }
            .background(colors.background)
            .clipShape(
                UnevenRoundedRectangle(
                    topLeadingRadius: 28,
                    bottomLeadingRadius: 0,
                    bottomTrailingRadius: 0,
                    topTrailingRadius: 28
                )
            )
            .shadow(color: Color.black.opacity(colors.scheme == .dark ? 0.5 : 0.15), radius: 10, x: 0, y: -5)
        }
        .ignoresSafeArea(edges: .bottom)
    }
}
