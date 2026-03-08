import SwiftUI

public struct SettingsView: View {
    @StateObject private var viewModel: SettingsViewModel
    
    public init(viewModel: SettingsViewModel) {
        _viewModel = StateObject(wrappedValue: viewModel)
    }
    
    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 32) {
                // AI Engine
                VStack(alignment: .leading, spacing: 16) {
                    SectionHeader("AI ENGINE")
                    
                    PreferenceItem(label: "Parsing Mode") {
                        Picker("", selection: Binding(
                            get: { viewModel.uiState.settings.parsingMode },
                            set: { viewModel.updateParsingMode($0) }
                        )) {
                            Text("Cloud").tag(ParsingMode.managed)
                            Text("BYOK").tag(ParsingMode.byok)
                        }
                        .pickerStyle(.segmented)
                    }
                    
                    if viewModel.uiState.settings.parsingMode == .byok {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("API Key")
                                .font(ScribbleFitFont.labelMedium())
                                .foregroundColor(ScribbleFitColor.secondaryText)
                            
                            ScribbleFitTextField(
                                text: Binding(
                                    get: { viewModel.uiState.apiKey },
                                    set: { viewModel.updateApiKey($0) }
                                ),
                                placeholder: "sk-..."
                            )
                        }
                        
                        PreferenceItem(label: "AI Provider") {
                            Picker("", selection: Binding(
                                get: { viewModel.uiState.settings.aiProvider },
                                set: { viewModel.updateProvider($0) }
                            )) {
                                Text("OpenAI").tag(LLMProvider.openai)
                                Text("Gemini").tag(LLMProvider.gemini)
                            }
                            .pickerStyle(.segmented)
                        }
                    }
                }
                
                Divider().background(ScribbleFitColor.divider)
                
                // Preferences
                VStack(alignment: .leading, spacing: 16) {
                    SectionHeader("PREFERENCES")
                    
                    PreferenceItem(label: "Weight Units") {
                        Picker("", selection: Binding(
                            get: { viewModel.uiState.settings.weightUnit },
                            set: { viewModel.updateWeightUnit($0) }
                        )) {
                            Text("lbs").tag(WeightUnit.lbs)
                            Text("kg").tag(WeightUnit.kg)
                        }
                        .pickerStyle(.segmented)
                    }
                    
                    PreferenceItem(label: "Appearance") {
                        Picker("", selection: Binding(
                            get: { viewModel.uiState.settings.themePreference },
                            set: { viewModel.updateTheme($0) }
                        )) {
                            Text("Light").tag(ThemePreference.light)
                            Text("Dark").tag(ThemePreference.dark)
                            Text("System").tag(ThemePreference.system)
                        }
                        .pickerStyle(.segmented)
                    }
                }
                
                // Danger Zone
                VStack(alignment: .leading, spacing: 16) {
                    SectionHeader("DANGER ZONE", color: Color.red)
                    
                    Button(action: viewModel.onClearDataClick) {
                        Text("Clear All Data")
                            .font(ScribbleFitFont.bodyLarge().bold())
                            .foregroundColor(.red)
                            .frame(maxWidth: .infinity, alignment: .leading)
                    }
                }
                
                Spacer()
            }
            .padding(.horizontal, ScribbleFitSpacing.screenPadding)
            .padding(.top, 20)
        }
        .background(ScribbleFitColor.background)
        .navigationTitle("Settings")
        .navigationBarTitleDisplayMode(.inline)
    }
    
    private func SectionHeader(_ title: String, color: Color = ScribbleFitColor.secondaryText) -> some View {
        Text(title)
            .font(ScribbleFitFont.labelMedium().bold())
            .kerning(0.8)
            .foregroundColor(color)
    }
    
    private func PreferenceItem<Content: View>(label: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 8) {
            Text(label)
                .font(ScribbleFitFont.bodyMedium())
                .foregroundColor(ScribbleFitColor.primaryText)
            content()
        }
    }
}
