import SwiftUI
import CoreDesignSystem
import CoreModel

public struct ScribbleDetailsView: View {
    @State private var store: ScribbleDetailsStore
    @Environment(\.dismiss) private var dismiss

    public init(store: ScribbleDetailsStore) {
        _store = State(initialValue: store)
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()
                
                if store.state.isLoading && store.state.scribble == nil {
                    ProgressView()
                        .tint(Color.scribblePrimary)
                } else if let scribble = store.state.scribble {
                    content(scribble)
                } else if let error = store.state.error {
                    VStack(spacing: 16) {
                        Text(error)
                            .foregroundStyle(Color.red)
                            .multilineTextAlignment(.center)
                        
                        Button("Retry") {
                            // Retry logic
                        }
                        .buttonStyle(.plain)
                    }
                    .padding()
                }
            }
            .navigationTitle("Scribble Details")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarLeading) {
                    Button {
                        dismiss()
                    } label: {
                        Image(systemName: "xmark")
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
            }
        }
    }

    private func content(_ scribble: Scribble) -> some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 24) {
                // Raw Text Header
                VStack(alignment: .leading, spacing: 8) {
                    Text("Scribble")
                        .font(.headline)
                        .foregroundStyle(Color.scribbleMidGray)
                    
                    Text(scribble.rawText)
                        .font(.body)
                        .padding()
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.scribbleSurface)
                        .clipShape(RoundedRectangle(cornerRadius: 12))
                }
                .padding(.horizontal)

                // Exercises List
                VStack(alignment: .leading, spacing: 16) {
                    Text("Exercises")
                        .font(.headline)
                        .foregroundStyle(Color.scribbleMidGray)
                        .padding(.horizontal)

                    ForEach(scribble.exercises) { exercise in
                        ExerciseRow(exercise: exercise, weightUnit: store.state.weightUnit)
                    }
                }

                Spacer(minLength: 100)
            }
            .padding(.top)
        }
        .safeAreaInset(edge: .bottom) {
            if store.state.canLog {
                Button {
                    store.onIntent(.logScribble)
                } label: {
                    if store.state.isLoading {
                        ProgressView()
                            .tint(.white)
                    } else {
                        Text("Confirm & Log")
                            .font(.headline)
                    }
                }
                .buttonStyle(PrimaryButtonStyle())
                .padding()
                .background(.ultraThinMaterial)
            }
        }
    }
}

private struct ExerciseRow: View {
    let exercise: Exercise
    let weightUnit: WeightUnit

    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(exercise.canonicalName)
                    .font(.subheadline)
                    .fontWeight(.semibold)
                Spacer()
                Text(exercise.muscleGroup)
                    .font(.caption)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(Color.scribblePrimary.opacity(0.1))
                    .foregroundStyle(Color.scribblePrimary)
                    .clipShape(Capsule())
            }

            Text(exercise.summary(weightUnit: weightUnit))
                .font(.caption)
                .foregroundStyle(Color.scribbleMidGray)
        }
        .padding()
        .background(Color.scribbleSurface)
        .clipShape(RoundedRectangle(cornerRadius: 12))
        .padding(.horizontal)
    }
}

// Minimal ButtonStyle implementation if CoreDesignSystem doesn't provide one
private struct PrimaryButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .frame(maxWidth: .infinity)
            .padding()
            .background(Color.scribblePrimary)
            .foregroundStyle(Color.scribbleOnPrimary)
            .clipShape(RoundedRectangle(cornerRadius: 12))
            .scaleEffect(configuration.isPressed ? 0.98 : 1)
            .opacity(configuration.isPressed ? 0.9 : 1)
    }
}
