import SwiftUI
import CoreModel
import CoreDesignSystem
#if canImport(UIKit)
import UIKit
#endif

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
                    VStack(spacing: 32) {
                        HeaderSection(onDismiss: onDismiss)
                        
                        VStack(spacing: 32) {
                            AppearanceSection(
                                theme: Binding(
                                    get: { store.state.config.themePreference },
                                    set: { store.onIntent(.updateTheme($0)) }
                                )
                            )
                            
                            AIConfigurationSection(
                                provider: Binding(
                                    get: { store.state.config.preferredLlmProvider },
                                    set: { store.onIntent(.updateLlmProvider($0)) }
                                ),
                                isLocalSupported: store.state.isLocalLlmSupported,
                                onIntent: store.onIntent
                            )
                            
                            UnitPreferencesSection(
                                unit: Binding(
                                    get: { store.state.config.weightUnit },
                                    set: { store.onIntent(.updateWeightUnit($0)) }
                                )
                            )
                            
                            DataManagementSection(
                                isExporting: store.state.isExporting,
                                onIntent: store.onIntent
                            )
                        }
                        .padding(.horizontal, 24)
                        
                        FooterSection()
                    }
                    .padding(.vertical, 24)
                }
                .scrollDismissesKeyboard(.interactively)
            }
            .toolbar(.hidden)
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
    }
}

// MARK: - Sections

private struct HeaderSection: View {
    let onDismiss: () -> Void
    
    var body: some View {
        HStack {
            Text(String(localized: "Settings"))
                .font(.scribbleHeadlineSmall)
                .foregroundStyle(Color.scribblePrimary)
            
            Spacer()
            
            Button(action: onDismiss) {
                Image(systemName: "xmark")
                    .font(.system(size: 18, weight: .medium))
                    .foregroundStyle(Color.scribbleMidGray)
                    .frame(width: 44, height: 44)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(Circle())
            }
        }
        .padding(.horizontal, 24)
    }
}

private struct AppearanceSection: View {
    @Binding var theme: ThemePreference
    
    var body: some View {
        SettingsContainer(title: String(localized: "Appearance")) {
            Picker(String(localized: "Theme"), selection: $theme) {
                Text(String(localized: "Light")).tag(ThemePreference.light)
                Text(String(localized: "Dark")).tag(ThemePreference.dark)
                Text(String(localized: "System")).tag(ThemePreference.system)
            }
            .pickerStyle(.segmented)
        }
    }
}

private struct AIConfigurationSection: View {
    @Binding var provider: LLMProvider
    let isLocalSupported: Bool
    let onIntent: (SettingsIntent) -> Void
    
    var body: some View {
        SettingsContainer(title: String(localized: "AI Configuration")) {
            VStack(spacing: 20) {
                // Provider Selection
                VStack(alignment: .leading, spacing: 12) {
                    Picker(String(localized: "Provider"), selection: $provider) {
                        Text(String(localized: "Local")).tag(LLMProvider.local)
                        Text(String(localized: "Cloud")).tag(LLMProvider.gemini)
                    }
                    .pickerStyle(.segmented)
                    
                    VStack(alignment: .leading, spacing: 4) {
                        Text(provider == .gemini ? String(localized: "Cloud-based parsing via Gemini for Firebase") : String(localized: "On-device parsing via Apple System Language Models"))
                            .font(.scribbleLabelMedium)
                            .foregroundStyle(Color.scribbleMidGray)
                    }
                    .padding(.horizontal, 4)
                    
                    if provider == .local && !isLocalSupported {
                        HStack(alignment: .top, spacing: 12) {
                            Image(systemName: "info.circle.fill")
                                .foregroundStyle(Color.scribblePrimary)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(String(localized: "LOCAL AI NOT SUPPORTED"))
                                    .font(.scribbleLabelMedium)
                                    .fontWeight(.bold)
                                    .foregroundStyle(Color.scribblePrimary)
                                
                                Text(String(localized: "Your device doesn't support on-device LLMs. Please switch to Cloud for AI features."))
                                    .font(.scribbleLabelMedium)
                                    .foregroundStyle(Color.scribbleMidGray)
                            }
                        }
                        .padding(16)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.scribbleSurfaceContainerLow)
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .transition(.opacity.combined(with: .move(edge: .top)))
                    }
                }
            }
        }
    }
}

private struct UnitPreferencesSection: View {
    @Binding var unit: WeightUnit
    
    var body: some View {
        SettingsContainer(title: String(localized: "Unit Preferences")) {
            Picker(String(localized: "Weight Unit"), selection: $unit) {
                Text(String(localized: "Kilograms (kg)")).tag(WeightUnit.kgs)
                Text(String(localized: "Pounds (lbs)")).tag(WeightUnit.lbs)
            }
            .pickerStyle(.segmented)
        }
    }
}

private struct DataManagementSection: View {
    let isExporting: Bool
    let onIntent: (SettingsIntent) -> Void
    
    var body: some View {
        SettingsContainer(title: String(localized: "Data Management")) {
            VStack(spacing: 12) {
                Button(action: { onIntent(.exportData) }) {
                    HStack {
                        if isExporting {
                            ProgressView()
                                .padding(.trailing, 8)
                        }
                        Text(String(localized: "EXPORT DATA AS JSON"))
                            .font(.scribbleLabelMedium)
                            .fontWeight(.bold)
                    }
                    .foregroundStyle(Color.scribblePrimary)
                    .frame(maxWidth: .infinity)
                    .frame(height: 48)
                    .background(Color.scribbleSurfaceContainerLow)
                    .clipShape(Capsule())
                }
                .disabled(isExporting)
                
                Button(action: { onIntent(.showClearConfirmation(true)) }) {
                    Text(String(localized: "CLEAR ALL DATA"))
                        .font(.scribbleLabelMedium)
                        .fontWeight(.bold)
                        .foregroundStyle(Color.red)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Color.red.opacity(0.05))
                        .clipShape(Capsule())
                }
            }
        }
    }
}

private struct FooterSection: View {
    var body: some View {
        VStack(spacing: 16) {
            Text(String(localized: "SCRIBBLEFIT VERSION 1.0 (2640)"))
                .font(.scribbleLabelMedium)
                .foregroundStyle(Color.scribbleMidGray)
            
            HStack(spacing: 24) {
                Button(String(localized: "Privacy Policy")) { }
                Button(String(localized: "Terms of Service")) { }
            }
            .font(.scribbleLabelMedium)
            .foregroundStyle(Color.scribbleMidGray)
            .opacity(0.6)
        }
        .padding(.top, 24)
    }
}

// MARK: - Sections

private struct SettingsContainer<Content: View>: View {
    let title: String
    let content: () -> Content
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text(title.uppercased())
                .font(.scribbleLabelMedium)
                .fontWeight(.bold)
                .kerning(1)
                .foregroundStyle(Color.scribbleMidGray)
            
            content()
        }
        .frame(maxWidth: .infinity, alignment: .leading)
    }
}

private struct IdentifiableURL: Identifiable {
    let id = UUID()
    let url: URL
}

#if os(iOS)
private struct ShareSheet: UIViewControllerRepresentable {
    let items: [Any]
    
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: items, applicationActivities: nil)
    }
    
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) { }
}
#endif
