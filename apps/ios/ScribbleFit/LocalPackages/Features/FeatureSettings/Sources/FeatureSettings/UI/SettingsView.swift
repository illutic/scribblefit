import SwiftUI
import CoreModel
import CoreDesignSystem

public struct SettingsView: View {
    @Bindable var store: SettingsStore
    let onDismiss: () -> Void
    
    public init(store: SettingsStore, onDismiss: @escaping () -> Void) {
        self.store = store
        self.onDismiss = onDismiss
    }

    public var body: some View {
        NavigationStack {
            ZStack {
                Color.scribbleBackground.ignoresSafeArea()
                
                ScrollView {
                    VStack(spacing: 16){
                        SettingsAppearanceSection(
                            theme: Binding(
                                get: { store.state.config.themePreference },
                                set: { store.onIntent(.updateTheme($0)) }
                            )
                        )
                        
                        SettingsAISection(
                            provider: Binding(
                                get: { store.state.config.preferredLlmProvider },
                                set: { store.onIntent(.updateLlmProvider($0)) }
                            ),
                            isLocalSupported: store.state.isLocalLlmSupported,
                            onIntent: store.onIntent
                        )
                        
                        SettingsUnitSection(
                            unit: Binding(
                                get: { store.state.config.weightUnit },
                                set: { store.onIntent(.updateWeightUnit($0)) }
                            )
                        )
                        
                        SettingsDataSection(
                            isExporting: store.state.isExporting,
                            onIntent: store.onIntent
                        )
                        
                        SettingsFooter(version: store.state.version)
                    }
                }
                .padding(.horizontal, 24)
            }
            #if os(iOS)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    Text(String(localized: "Settings"))
                        .font(.scribbleHeadlineSmall)
                        .foregroundStyle(Color.scribblePrimary)
                }
                ToolbarItem(placement: .topBarTrailing) {
                    Button(action: onDismiss) {
                        Image(systemName: "xmark")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundStyle(Color.scribblePrimary)
                    }
                }
            }
            #endif
            #if os(iOS)
            .navigationBarTitleDisplayMode(.inline)
            #endif
            .alert(String(localized: "Clear All Data"), isPresented: $store.state.isShowingClearConfirmation) {
                Button(String(localized: "Cancel"), role: .cancel) { }
                Button(String(localized: "Clear Everything"), role: .destructive) {
                    store.onIntent(.clearAllData)
                }
            } message: {
                Text(String(localized: "This will permanently delete all your workout history, scribbles, and settings. This action cannot be undone."))
            }
            #if os(iOS)
            .sheet(item: Binding(
                get: { store.state.exportURL.map { IdentifiableURL(url: $0) } },
                set: { _ in store.state.exportURL = nil }
            )) { identifiableURL in
                ShareSheet(items: [identifiableURL.url])
            }
            #endif
        }
        .colorScheme(store.state.config.themePreference.getColorScheme())
        .presentationDetents([.medium, .large])
        .presentationDragIndicator(.visible)
    }
}
