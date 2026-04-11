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
                                apiKey: $store.state.apiKey,
                                isApiKeyVisible: store.state.isApiKeyVisible,
                                isTesting: store.state.isTestingConnection,
                                testResult: store.state.connectionTestResult,
                                availableModels: store.state.availableModels,
                                preferredModel: store.state.config.preferredModel,
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
    @Binding var apiKey: String
    let isApiKeyVisible: Bool
    let isTesting: Bool
    let testResult: SettingsState.ConnectionTestResult?
    let availableModels: [String]
    let preferredModel: String?
    let isLocalSupported: Bool
    let onIntent: (SettingsIntent) -> Void
    
    var body: some View {
        SettingsContainer(title: String(localized: "AI Configuration")) {
            VStack(spacing: 20) {
                // Provider Selection
                VStack(alignment: .leading, spacing: 12) {
                    Picker(String(localized: "Provider"), selection: $provider) {
                        Text(String(localized: "Local")).tag(LLMProvider.local)
                        Text(String(localized: "Cloud (Gemini)")).tag(LLMProvider.gemini)
                    }
                    .pickerStyle(.segmented)
                    
                    if provider == .local && !isLocalSupported {
                        HStack(alignment: .top, spacing: 12) {
                            Image(systemName: "info.circle.fill")
                                .foregroundStyle(Color.scribblePrimary)
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text(String(localized: "LOCAL AI NOT SUPPORTED"))
                                    .font(.scribbleLabelMedium)
                                    .fontWeight(.bold)
                                    .foregroundStyle(Color.scribblePrimary)
                                
                                Text(String(localized: "Your device doesn't support on-device LLMs. Please switch to Cloud (Gemini) for AI features."))
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
                
                if provider == .gemini {
                    VStack(alignment: .leading, spacing: 16) {
                        // API Key Input
                        VStack(alignment: .leading, spacing: 8) {
                            Text(String(localized: "GEMINI API KEY"))
                                .font(.scribbleLabelMedium)
                                .foregroundStyle(Color.scribbleMidGray)
                            
                            HStack {
                                if isApiKeyVisible {
                                    TextField(String(localized: "Enter Key"), text: $apiKey)
                                        .onChange(of: apiKey) { _, newValue in
                                            onIntent(.updateApiKey(newValue))
                                        }
                                } else {
                                    SecureField(String(localized: "Enter Key"), text: $apiKey)
                                        .onChange(of: apiKey) { _, newValue in
                                            onIntent(.updateApiKey(newValue))
                                        }
                                }
                                
                                Button(action: { onIntent(.toggleApiKeyVisibility) }) {
                                    Image(systemName: isApiKeyVisible ? "eye.slash" : "eye")
                                        .foregroundStyle(Color.scribbleMidGray)
                                }
                            }
                            .padding(.horizontal, 16)
                            .frame(height: 48)
                            .background(Color.scribbleSurfaceContainerLow)
                            .clipShape(Capsule())
                        }
                        
                        // Test Connection Button
                        Button(action: { onIntent(.testConnection) }) {
                            HStack {
                                if isTesting {
                                    ProgressView()
                                        .tint(.scribbleOnPrimary)
                                        .padding(.trailing, 8)
                                }
                                
                                Text(connectionText)
                                    .font(.scribbleLabelMedium)
                                    .fontWeight(.bold)
                            }
                            .foregroundStyle(Color.scribbleOnPrimary)
                            .frame(maxWidth: .infinity)
                            .frame(height: 48)
                            .background(Color.scribblePrimary)
                            .clipShape(Capsule())
                        }
                        .disabled(isTesting || apiKey.isEmpty)
                        
                        // Model Selection
                        if !availableModels.isEmpty {
                            VStack(alignment: .leading, spacing: 8) {
                                Text(String(localized: "PREFERRED MODEL"))
                                    .font(.scribbleLabelMedium)
                                    .foregroundStyle(Color.scribbleMidGray)
                                
                                Menu {
                                    ForEach(availableModels, id: \.self) { model in
                                        Button(action: { onIntent(.updatePreferredModel(model)) }) {
                                            HStack {
                                                Text(model)
                                                if model == preferredModel {
                                                    Image(systemName: "checkmark")
                                                }
                                            }
                                        }
                                    }
                                } label: {
                                    HStack {
                                        Text(preferredModel ?? String(localized: "Select Model"))
                                            .font(.scribbleBodyMedium)
                                        Spacer()
                                        Image(systemName: "chevron.up.down")
                                            .font(.system(size: 12))
                                    }
                                    .foregroundStyle(Color.scribblePrimary)
                                    .padding(.horizontal, 16)
                                    .frame(height: 48)
                                    .background(Color.scribbleSurfaceContainerLow)
                                    .clipShape(Capsule())
                                }
                            }
                        }
                    }
                    .transition(.opacity.combined(with: .move(edge: .top)))
                }
            }
        }
    }
    
    private var connectionText: String {
        if isTesting { return String(localized: "TESTING...") }
        switch testResult {
        case .success: return String(localized: "SUCCESS")
        case .failure: return String(localized: "ERROR - RETRY")
        case .none: return String(localized: "TEST CONNECTION")
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
