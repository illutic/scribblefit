import SwiftUI

public struct CanvasView: View {
    @State private var store: CanvasStore
    @Environment(\.scribbleFitColors) var colors
    
    public init(store: CanvasStore) {
        _store = State(initialValue: store)
    }

    public var body: some View {
        ZStack {
            colors.background
                .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Bar
                HStack {
                    Text(store.state.appName)
                        .font(.system(size: 22, weight: .bold))
                        .foregroundColor(colors.richBlack)
                    Spacer()
                    Button(action: { store.onIntent(.navigateToProfile) }) {
                        Image(systemName: "person.fill")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(colors.background)
                            .padding(8)
                            .background(colors.richBlack)
                            .clipShape(Circle())
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                // Date Header
                HStack {
                    Button(action: { store.onIntent(.onPreviousDayClick) }) {
                        Image(systemName: "chevron.left")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(colors.richBlack)
                            .padding(8)
                    }
                    .glassEffect(.regular.interactive(), in: .circle)
                    
                    Spacer()
                    
                    Text(store.state.dateString.uppercased())
                        .font(.system(size: 12, weight: .medium))
                        .foregroundColor(colors.midGray)
                    
                    Spacer()
                    
                    Button(action: { store.onIntent(.onNextDayClick) }) {
                        Image(systemName: "chevron.right")
                            .font(.system(size: 20, weight: .medium))
                            .foregroundColor(store.state.isCurrentDate ? colors.midGray : colors.richBlack)
                            .padding(8)
                    }
                    .glassEffect(.regular.interactive(), in: .circle)
                    .disabled(store.state.isCurrentDate)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 8)

                // Scribbles List
                ScrollView {
                    LazyVStack(spacing: 16) {
                        if store.state.scribbles.isEmpty {
                            Box {
                                Text(store.state.emptyScribbleText)
                                    .font(.system(size: 16))
                                    .foregroundColor(colors.midGray)
                                    .multilineTextAlignment(.center)
                                    .padding(.horizontal, 32)
                            }
                            .frame(minHeight: 400)
                        } else {
                            ForEach(store.state.scribbles) { scribble in
                                ScribbleRow(scribble: scribble) {
                                    store.onIntent(.clickOnScribble(scribble))
                                }
                            }
                        }
                    }
                    .padding(16)
                }

                // Input Area (Native .glassEffect)
                VStack(spacing: 0) {
                    HStack(alignment: .center) {
                        TextField(store.state.textfieldPlaceholder, text: Binding(
                            get: { store.state.currentScribbleText },
                            set: { store.onIntent(.updateScribbleText($0)) }
                        ))
                        .font(.system(size: 15))
                        .padding(.leading, 18)
                        .foregroundColor(colors.richBlack)
                        
                        Button(action: { store.onIntent(.addScribble(store.state.currentScribbleText)) }) {
                            Image(systemName: "arrow.up")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(colors.background)
                                .padding(6)
                                .background(store.state.currentScribbleText.isEmpty ? colors.midGray : colors.richBlack)
                                .clipShape(Circle())
                        }
                        .disabled(store.state.currentScribbleText.isEmpty)
                        .padding(.trailing, 10)
                    }
                    .frame(height: 52)
                    .glassEffect(.regular.interactive(), in: .capsule)
                    .padding(.horizontal, 16)
                    .padding(.bottom, 16)
                }
            }
            .blur(radius: store.state.selectedScribble != nil ? 10 : 0)
            .disabled(store.state.selectedScribble != nil)

            if let scribble = store.state.selectedScribble {
                Color.black.opacity(0.4)
                    .ignoresSafeArea()
                    .onTapGesture {
                        store.onIntent(.dismissScribbleDialog)
                    }
                
                ScribbleDialog(
                    scribble: scribble,
                    onConfirm: { store.onIntent(.confirmScribble(scribble)) },
                    onDismiss: { store.onIntent(.dismissScribbleDialog) },
                    onDelete: { store.onIntent(.deleteScribble(scribble)) },
                    onEdit: { store.onIntent(.updateScribble(scribble)) }
                )
                .transition(.move(edge: .bottom).combined(with: .opacity))
            }
        }
        .animation(.spring(), value: store.state.selectedScribble)
    }
}

struct Box<Content: View>: View {
    let content: Content
    init(@ViewBuilder content: () -> Content) {
        self.content = content()
    }
    var body: some View {
        content
    }
}

struct ScribbleRow: View {
    let scribble: Scribble
    let onClick: () -> Void
    @Environment(\.scribbleFitColors) var colors

    var body: some View {
        Button(action: { onClick() }) {
            VStack(alignment: .leading, spacing: 8) {
                if scribble.status == .completed {
                    HStack {
                        Spacer()
                        Badge(text: "COMPLETED", backgroundColor: colors.successGreen.opacity(0.2), contentColor: colors.successGreen)
                    }
                } else if scribble.status == .failed {
                    HStack {
                        Text(scribble.rawText)
                            .font(.system(size: 16))
                            .foregroundColor(colors.richBlack)
                        Spacer()
                        Badge(text: "FAILED", backgroundColor: colors.dangerRed.opacity(0.2), contentColor: colors.dangerRed)
                    }
                } else if scribble.status == .raw {
                    HStack {
                        Text(scribble.rawText)
                            .font(.system(size: 16))
                            .foregroundColor(colors.richBlack)
                        Spacer()
                        Text("PENDING")
                            .font(.system(size: 11, weight: .bold))
                            .foregroundColor(colors.strongGray)
                    }
                }

                if scribble.status == .completed || scribble.status == .parsed {
                    ExerciseItems(exercises: scribble.exercises)
                }
            }
            .padding(16)
            .frame(maxWidth: .infinity, alignment: .leading)
            .background(colors.background)
            .cornerRadius(12)
            .shadow(color: Color.black.opacity(colors.scheme == .dark ? 0.3 : 0.1), radius: 2, x: 0, y: 1)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(borderColor, lineWidth: 1)
            )
        }
        .buttonStyle(.plain)
    }
    
    private var borderColor: Color {
        switch scribble.status {
        case .completed: return colors.successGreen.opacity(0.3)
        case .failed: return colors.dangerRed.opacity(0.3)
        case .parsed: return colors.lightGray
        default: return .clear
        }
    }
}

struct ExerciseItems: View {
    let exercises: [Exercise]
    @Environment(\.scribbleFitColors) var colors
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            ForEach(exercises) { exercise in
                VStack(alignment: .leading, spacing: 4) {
                    Text(exercise.name)
                        .font(.system(size: 16, weight: .bold))
                        .foregroundColor(colors.richBlack)
                    
                    ForEach(Array(exercise.sets.enumerated()), id: \.offset) { index, set in
                        Text("\(index + 1). \(String(format: "%.1f", set.weight))kg x \(set.reps)")
                            .font(.system(size: 14))
                            .foregroundColor(colors.strongGray)
                    }
                }
            }
        }
    }
}

struct Badge: View {
    let text: String
    let backgroundColor: Color
    let contentColor: Color
    
    var body: some View {
        Text(text)
            .font(.system(size: 11, weight: .bold))
            .padding(.horizontal, 6)
            .padding(.vertical, 2)
            .background(backgroundColor)
            .foregroundColor(contentColor)
            .cornerRadius(4)
    }
}
