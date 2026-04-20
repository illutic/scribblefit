import SwiftUI
import CoreModel
import CoreDesignSystem
import FeatureSettings

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
                
                CanvasBodyView(
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
                CanvasDatePickerView(
                    initialDate: store.state.currentDate,
                    onDateSelected: { store.onIntent(.onDateSelected($0)) },
                    onDismiss: { store.onIntent(.dismissDatePicker) }
                )
            }
            .sheet(isPresented: Binding(
                get: { store.state.selectedScribble != nil },
                set: { if !$0 { store.onIntent(.dismissScribbleDialog) } }
            )) {
                if let scribble = store.state.selectedScribble {
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
