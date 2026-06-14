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

                    PreferenceItem(label: "Provider") {
                        Menu {
                            Button("Gemini") { viewModel.updateProvider(.gemini) }
                            Button("Local") { viewModel.updateProvider(.local) }
                        } label: {
                            HStack {
                                Text(providerLabel(viewModel.uiState.settings.aiProvider))
                                    .font(ScribbleFitFont.bodyMedium())
                                    .foregroundColor(ScribbleFitColor.primaryText)
                                Spacer()
                                Image(systemName: "chevron.up.chevron.down")
                                    .font(.system(size: 12))
                                    .foregroundColor(ScribbleFitColor.secondaryText)
                            }
                            .padding(.horizontal, 12)
                            .padding(.vertical, 10)
                            .background(ScribbleFitColor.secondaryBackground)
                            .cornerRadius(ScribbleFitShape.medium)
                        }
                    }

                    let provider = viewModel.uiState.settings.aiProvider
                    if provider == .gemini {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("API Key")
                                .font(ScribbleFitFont.labelMedium())
                                .foregroundColor(ScribbleFitColor.secondaryText)

                            ScribbleFitTextField(
                                text: Binding(
                                    get: { viewModel.uiState.apiKey },
                                    set: { viewModel.updateApiKey($0) }
                                ),
                                placeholder: "AIza..."
                            )
                        }

                        PreferenceItem(label: "Model") {
                            if viewModel.uiState.isLoadingModels {
                                HStack {
                                    ProgressView().controlSize(.small)
                                    Text("Loading models...")
                                        .font(ScribbleFitFont.bodyMedium())
                                        .foregroundColor(ScribbleFitColor.secondaryText)
                                }
                            } else if viewModel.uiState.availableModels.isEmpty {
                                Button(action: { Task { await viewModel.fetchModels() } }) {
                                    Text(viewModel.uiState.apiKey.isEmpty ? "Enter API key to load models" : (viewModel.uiState.modelLoadError ?? "Tap to load models"))
                                        .font(ScribbleFitFont.bodyMedium())
                                        .foregroundColor(viewModel.uiState.modelLoadError != nil ? .red : ScribbleFitColor.secondaryText)
                                }
                            } else {
                                Menu {
                                    ForEach(viewModel.uiState.availableModels, id: \.self) { model in
                                        Button(model) { viewModel.updateModel(model) }
                                    }
                                } label: {
                                    HStack {
                                        Text(viewModel.uiState.settings.selectedModel ?? viewModel.uiState.availableModels.first ?? "Select model")
                                            .font(ScribbleFitFont.bodyMedium())
                                            .foregroundColor(ScribbleFitColor.primaryText)
                                        Spacer()
                                        Image(systemName: "chevron.up.chevron.down")
                                            .font(.system(size: 12))
                                            .foregroundColor(ScribbleFitColor.secondaryText)
                                    }
                                    .padding(.horizontal, 12)
                                    .padding(.vertical, 10)
                                    .background(ScribbleFitColor.secondaryBackground)
                                    .cornerRadius(ScribbleFitShape.medium)
                                }
                            }
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

    private func providerLabel(_ provider: LLMProvider) -> String {
        switch provider {
        case .gemini: return "Gemini"
        case .local: return "Local"
        }
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
