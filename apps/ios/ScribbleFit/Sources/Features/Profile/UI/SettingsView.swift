import SwiftUI

public struct SettingsView: View {
    @ObservedObject private var viewModel: SettingsViewModel

    public init(viewModel: SettingsViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        NavigationStack {
            Form {
                Section("AI Provider") {
                    Picker("Provider", selection: Binding(
                        get: { viewModel.uiState.settings.aiProvider },
                        set: { provider in Task { await viewModel.onProviderChanged(provider) } }
                    )) {
                        Text("Proxy (Default)").tag(LLMProvider.proxy)
                        Text("Gemini").tag(LLMProvider.gemini)
                        Text("OpenAI").tag(LLMProvider.openai)
                    }
                    if viewModel.uiState.showApiKeyField {
                        SecureField("API Key", text: Binding(
                            get: { viewModel.uiState.apiKey },
                            set: { _ in }
                        ))
                        .onSubmit { Task { await viewModel.onApiKeySaved(viewModel.uiState.apiKey) } }
                    }
                }
                Section("Units") {
                    Picker("Weight", selection: Binding(
                        get: { viewModel.uiState.settings.weightUnit },
                        set: { unit in Task { await viewModel.onWeightUnitChanged(unit) } }
                    )) {
                        Text("lbs").tag(WeightUnit.lbs)
                        Text("kg").tag(WeightUnit.kg)
                    }
                }
                Section {
                    Button("Clear All Data", role: .destructive) {
                        Task { await viewModel.onClearDataTapped() }
                    }
                }
            }
            .navigationTitle("Settings")
            .task { await viewModel.loadSettings() }
        }
    }
}
