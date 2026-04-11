import SwiftUI
#if SWIFT_PACKAGE
import CoreModel
import CoreDesignSystem
import FeatureSettings
#endif

public struct CanvasView: View {
    @Bindable var store: CanvasStore
    let settingsStore: SettingsStore
    
    public init(store: CanvasStore, settingsStore: SettingsStore) {
        self.store = store
        self.settingsStore = settingsStore
    }

    public var body: some View {
        NavigationStack {
            ZStack(alignment: .bottom) {
                Color.scribbleBackground.ignoresSafeArea()
                
                BodyView(
                    scribbles: store.state.scribbles,
                    aiInsights: store.state.aiInsights,
                    isGeneratingInsights: store.state.isGeneratingInsights,
                    weightUnit: store.state.weightUnit,
                    emptyText: store.state.emptyScribbleText,
                    onScribbleClick: { store.onIntent(.clickOnScribble($0)) },
                    onIntent: store.onIntent
                )
                
                VStack(spacing: 0) {
                    Spacer()
                    ScribbleInputBar(
                        text: $store.state.currentScribbleText,
                        placeholder: store.state.textfieldPlaceholder,
                        onSend: { store.onIntent(.addScribble(store.state.currentScribbleText)) }
                    )
                    .padding(.horizontal, 16)
                    .padding(.bottom, 12)
                }
            }
            .toolbar {
                ToolbarItem(placement: .principal) {
                    HStack(spacing: 12) {
                        Button(action: { store.onIntent(.onPreviousDayClick) }) {
                            Image(systemName: "chevron.left")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(Color.scribbleMidGray)
                        }
                        
                        Button(action: { store.onIntent(.showDatePicker) }) {
                            Text(store.state.dateString)
                                .font(.scribbleBodyMedium)
                                .fontWeight(.semibold)
                                .foregroundStyle(Color.scribblePrimary)
                        }
                        
                        Button(action: { store.onIntent(.onNextDayClick) }) {
                            Image(systemName: "chevron.right")
                                .font(.system(size: 14, weight: .bold))
                                .foregroundStyle(Color.scribbleMidGray)
                        }
                        .disabled(store.state.isCurrentDate)
                        .opacity(store.state.isCurrentDate ? 0.3 : 1.0)
                    }
                }
                
                #if os(iOS)
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: { store.onIntent(.navigateToSettings) }) {
                        Image(systemName: "gearshape")
                            .font(.system(size: 20))
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
                #endif
            }
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .sheet(isPresented: $store.state.isDatePickerVisible) {
                DatePickerView(
                    initialDate: store.state.currentDate,
                    onDateSelected: { store.onIntent(.onDateSelected($0)) },
                    onDismiss: { store.onIntent(.dismissDatePicker) }
                )
            }
            .sheet(item: $store.state.selectedScribble) { scribble in
                ScribbleConfirmationBottomSheet(
                    scribble: scribble,
                    weightUnit: store.state.weightUnit,
                    onConfirm: { store.onIntent(.confirmScribble($0)) },
                    onDelete: { store.onIntent(.deleteScribble($0.id)) },
                    onDismiss: { store.onIntent(.dismissScribbleDialog) },
                    onUpdateExerciseName: { id, name in store.onIntent(.updateExerciseName(id, name)) },
                    onUpdateSetWeight: { exId, setId, weight in store.onIntent(.updateSetWeight(exId, setId, weight)) },
                    onUpdateSetReps: { exId, setId, reps in store.onIntent(.updateSetReps(exId, setId, reps)) },
                    onDeleteSet: { exId, setId in store.onIntent(.deleteSet(exId, setId)) }
                )
            }
            #if os(iOS)
            .fullScreenCover(isPresented: $store.state.isSettingsVisible) {
                SettingsView(store: settingsStore) {
                    store.state.isSettingsVisible = false
                }
            }
            #endif
        }
    }
}

struct ScribbleInputBar: View {
    @Binding var text: String
    let placeholder: String
    let onSend: () -> Void
    
    var body: some View {
        HStack(spacing: 12) {
            TextField(placeholder, text: $text)
                .font(.scribbleBodyMedium)
                .padding(.leading, 16)
                .submitLabel(.send)
                .onSubmit(onSend)
            
            if !text.isEmpty {
                Button(action: onSend) {
                    Image(systemName: "arrow.up.circle.fill")
                        .font(.system(size: 32))
                        .foregroundStyle(Color.scribblePrimary)
                }
                .padding(.trailing, 4)
                .transition(.scale.combined(with: .opacity))
            }
        }
        .frame(height: 52)
        .scribbleGlass()
        .clipShape(Capsule())
        .overlay {
            Capsule()
                .stroke(Color.scribblePrimary.opacity(0.1), lineWidth: 0.5)
        }
        .shadow(color: .black.opacity(0.1), radius: 10, x: 0, y: 5)
        .animation(.spring(response: 0.3, dampingFraction: 0.7), value: text.isEmpty)
    }
}

private struct BodyView: View {
    let scribbles: [Scribble]
    let aiInsights: [AIInsight]
    let isGeneratingInsights: Bool
    let weightUnit: WeightUnit
    let emptyText: String
    let onScribbleClick: (Scribble) -> Void
    let onIntent: (CanvasIntent) -> Void
    
    var body: some View {
        ScrollView {
            VStack(spacing: 40) {
                if isGeneratingInsights {
                    AIInsightsLoadingView()
                } else if !aiInsights.isEmpty {
                    AIInsightsList(insights: aiInsights)
                }
                
                if scribbles.isEmpty {
                    VStack(spacing: 24) {
                        Spacer().frame(height: 60)
                        Text(emptyText)
                            .font(.scribbleHeadlineSmall)
                            .multilineTextAlignment(.center)
                            .foregroundStyle(Color.scribbleMidGray)
                            .lineSpacing(8)
                    }
                    .padding(.horizontal, 40)
                } else {
                    LazyVStack(spacing: 24) {
                        ForEach(scribbles) { scribble in
                            ScribbleCard(
                                scribble: scribble,
                                weightUnit: weightUnit,
                                onClick: { onScribbleClick(scribble) },
                                onIntent: onIntent
                            )
                        }
                    }
                }
                
                Spacer().frame(height: 80) // Bottom padding for floating input bar
            }
            .padding(.horizontal, 24)
            .padding(.top, 24)
        }
        .scrollDismissesKeyboard(.interactively)
    }
}

private struct AIInsightsLoadingView: View {
    @State private var isAnimating = false
    
    var body: some View {
        VStack(spacing: 16) {
            ForEach(0..<1, id: \.self) { _ in
                HStack(alignment: .top, spacing: 16) {
                    Circle()
                        .fill(Color.scribblePrimary.opacity(0.1))
                        .frame(width: 32, height: 32)
                    
                    VStack(alignment: .leading, spacing: 8) {
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribbleMidGray.opacity(0.2))
                            .frame(width: 80, height: 12)
                        
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribblePrimary.opacity(0.1))
                            .frame(maxWidth: .infinity)
                            .frame(height: 16)
                        
                        RoundedRectangle(cornerRadius: 4)
                            .fill(Color.scribblePrimary.opacity(0.1))
                            .frame(width: 200, height: 16)
                    }
                }
                .padding(16)
                .scribbleGlass(cornerRadius: 16)
                .opacity(isAnimating ? 0.5 : 1.0)
            }
        }
        .onAppear {
            withAnimation(.easeInOut(duration: 1.0).repeatForever(autoreverses: true)) {
                isAnimating = true
            }
        }
    }
}

private struct AIInsightsList: View {
    let insights: [AIInsight]

    var body: some View {
        VStack(spacing: 16) {
            ForEach(insights) { insight in
                HStack(alignment: .top, spacing: 16) {
                    Image(systemName: insight.iconName)
                        .font(.system(size: 18, weight: .bold))
                        .foregroundStyle(Color.scribblePrimary)
                        .frame(width: 32, height: 32)
                        .background(Color.scribblePrimary.opacity(0.1))
                        .clipShape(Circle())
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(insight.insightType.rawValue.uppercased())
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                            .kerning(1)
                            .foregroundStyle(Color.scribbleMidGray)
                        
                        Text(insight.text)
                            .font(.scribbleBodyMedium)
                            .fontWeight(.medium)
                            .foregroundStyle(Color.scribblePrimary)
                            .lineSpacing(4)
                    }
                    
                    Spacer()
                }
                .padding(16)
                .scribbleGlass(cornerRadius: 16)
            }
        }
    }
}

extension AIInsight {
    var iconName: String {
        switch insightType {
        case .summary: return "sparkles"
        case .trend: return "chart.line.uptrend.xyaxis"
        case .advice: return "lightbulb.fill"
        }
    }
}

private struct DatePickerView: View {
    let initialDate: Date
    let onDateSelected: (Date) -> Void
    let onDismiss: () -> Void
    
    @State private var selection: Date
    
    init(initialDate: Date, onDateSelected: @escaping (Date) -> Void, onDismiss: @escaping () -> Void) {
        self.initialDate = initialDate
        self.onDateSelected = onDateSelected
        self.onDismiss = onDismiss
        self._selection = State(initialValue: initialDate)
    }
    
    var body: some View {
        NavigationStack {
            ScrollView {
                VStack {
                    DatePicker(
                        String(localized: "Select Date"),
                        selection: $selection,
                        in: ...Date(),
                        displayedComponents: .date
                    )
                    .datePickerStyle(.graphical)
                    .tint(.scribblePrimary)
                    .padding()
                    
                    Spacer()
                }
            }
            .navigationTitle(String(localized: "Calendar"))
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .toolbar {
                #if os(iOS)
                ToolbarItem(placement: .topBarLeading) {
                    Button(String(localized: "Cancel")) { onDismiss() }
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(String(localized: "Done")) { onDateSelected(selection) }
                }
                #else
                ToolbarItem(placement: .cancellationAction) {
                    Button(String(localized: "Cancel")) { onDismiss() }
                }
                ToolbarItem(placement: .confirmationAction) {
                    Button(String(localized: "Done")) { onDateSelected(selection) }
                }
                #endif
            }
        }
        .presentationDetents([.medium])
    }
}
