import SwiftUI

public struct SettingsView: View {
    @ObservedObject private var viewModel: SettingsViewModel

    @State private var showApiKeyInput: Bool = false
    @State private var apiKeyInput: String = ""
    @State private var showClearConfirm: Bool = false
    @State private var showProviderPicker: Bool = false
    @State private var showThemePicker: Bool = false
    @State private var showModelPicker: Bool = false

    public init(viewModel: SettingsViewModel) {
        self.viewModel = viewModel
    }

    public var body: some View {
        ScrollView {
            VStack(alignment: .leading, spacing: 0) {
                // Screen title
                Text("Settings")
                    .font(.system(size: 28, weight: .semibold))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                    .padding(.leading, ScribbleFitSpacing.medium)
                    .padding(.top, ScribbleFitSpacing.large)
                    .padding(.bottom, ScribbleFitSpacing.large)

                // MARK: — Section 1: AI ENGINE
                sectionHeader("AI ENGINE")

                VStack(spacing: 0) {
                    // Provider row
                    settingsRow(
                        label: "Provider",
                        value: providerDisplayString(viewModel.uiState.config.preferredLlmProvider)
                    ) {
                        showProviderPicker = true
                    }
                    // Provider picker action sheet
                    .confirmationDialog("Select Provider", isPresented: $showProviderPicker, titleVisibility: .visible) {
                        Button("Proxy (Default)") { Task { await viewModel.onProviderChanged(.proxy) } }
                        Button("Gemini") { Task { await viewModel.onProviderChanged(.gemini) } }
                        Button("OpenAI") { Task { await viewModel.onProviderChanged(.openai) } }
                        Button("Local") { Task { await viewModel.onProviderChanged(.local) } }
                        Button("Cancel", role: .cancel) {}
                    }

                    // Model row — only shown when provider requires an API key
                    if viewModel.uiState.config.preferredLlmProvider != .proxy && viewModel.uiState.config.preferredLlmProvider != .local {
                        rowDivider()

                        settingsRow(
                            label: "Model",
                            value: modelDisplayString(viewModel.uiState.config.preferredModel)
                        ) {
                            if !viewModel.uiState.availableModels.isEmpty {
                                showModelPicker = true
                            }
                        }
                        // Model picker action sheet
                        .confirmationDialog("Select Model", isPresented: $showModelPicker, titleVisibility: .visible) {
                            ForEach(viewModel.uiState.availableModels, id: \.self) { model in
                                Button(model) { viewModel.onModelSelected(model) }
                            }
                            Button("Cancel", role: .cancel) {}
                        }
                    }

                    // API Key row — only shown when provider requires an API key
                    if viewModel.uiState.config.preferredLlmProvider != .proxy && viewModel.uiState.config.preferredLlmProvider != .local {
                        rowDivider()

                        apiKeyRow()
                    }
                }

                // MARK: — Section 2: PREFERENCES
                sectionHeader("PREFERENCES")

                VStack(spacing: 0) {
                    // Weight Unit row
                    weightUnitRow()

                    rowDivider()

                    // Theme row
                    settingsRow(
                        label: "Theme",
                        value: themeDisplayString(viewModel.uiState.config.themePreference)
                    ) {
                        showThemePicker = true
                    }
                    // Theme picker action sheet
                    .confirmationDialog("Select Theme", isPresented: $showThemePicker, titleVisibility: .visible) {
                        Button("Light") { Task { await viewModel.onThemeChanged(.light) } }
                        Button("Dark") { Task { await viewModel.onThemeChanged(.dark) } }
                        Button("System") { Task { await viewModel.onThemeChanged(.system) } }
                        Button("Cancel", role: .cancel) {}
                    }
                }

                // MARK: — Section 3: DATA
                sectionHeader("DATA")

                VStack(spacing: 0) {
                    // Clear All Data row
                    Button {
                        showClearConfirm = true
                    } label: {
                        HStack {
                            Text("Clear All Data")
                                .font(.system(size: 17, weight: .regular))
                                .foregroundStyle(ScribbleFitColor.dangerRed)
                            Spacer()
                        }
                        .padding(.horizontal, ScribbleFitSpacing.medium)
                        .padding(.vertical, ScribbleFitSpacing.medium)
                    }
                    // Clear data confirmation dialog
                    .confirmationDialog("Clear All Data", isPresented: $showClearConfirm, titleVisibility: .visible) {
                        Button("Clear All Data", role: .destructive) { Task { await viewModel.onClearDataTapped() } }
                        Button("Cancel", role: .cancel) {}
                    } message: {
                        Text("This will permanently delete all your workout data. This action cannot be undone.")
                    }
                    rowDivider()
                }

                Spacer(minLength: ScribbleFitSpacing.large)
            }
        }
        .background(ScribbleFitColor.background)
        .task { await viewModel.loadConfig() }
    }

    // MARK: - Section Header

    @ViewBuilder
    private func sectionHeader(_ title: String) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(title)
                .font(.system(size: 12, weight: .semibold))
                .foregroundStyle(ScribbleFitColor.midGray)
                .padding(.leading, ScribbleFitSpacing.medium)
                .padding(.top, ScribbleFitSpacing.large)
                .padding(.bottom, ScribbleFitSpacing.small)

            Color(ScribbleFitColor.lightGray)
                .frame(height: 1)
        }
    }

    // MARK: - Row Helpers

    @ViewBuilder
    private func rowDivider() -> some View {
        Color(ScribbleFitColor.lightGray)
            .frame(height: 1)
            .padding(.leading, ScribbleFitSpacing.medium)
    }

    @ViewBuilder
    private func settingsRow(label: String, value: String, onTap: @escaping () -> Void) -> some View {
        Button(action: onTap) {
            HStack {
                Text(label)
                    .font(.system(size: 17, weight: .regular))
                    .foregroundStyle(ScribbleFitColor.richBlack)

                Spacer()

                Text(value)
                    .font(.system(size: 15, weight: .regular))
                    .foregroundStyle(ScribbleFitColor.midGray)

                Image(systemName: "chevron.right")
                    .font(.system(size: 12))
                    .foregroundStyle(ScribbleFitColor.midGray)
            }
            .padding(.horizontal, ScribbleFitSpacing.medium)
            .padding(.vertical, ScribbleFitSpacing.medium)
        }
    }

    // MARK: - API Key Row

    @ViewBuilder
    private func apiKeyRow() -> some View {
        VStack(spacing: 0) {
            HStack {
                Text("API Key")
                    .font(.system(size: 17, weight: .regular))
                    .foregroundStyle(ScribbleFitColor.richBlack)

                Spacer()

                // Masked key or "Not set"
                Text(maskedApiKey(viewModel.uiState.apiKey))
                    .font(.system(size: 15, weight: .regular))
                    .foregroundStyle(ScribbleFitColor.midGray)

                // Save Key pill button
                Button("Save Key") {
                    showApiKeyInput.toggle()
                }
                .font(.system(size: 15, weight: .semibold))
                .foregroundStyle(ScribbleFitColor.richBlack)
                .padding(.horizontal, 12)
                .padding(.vertical, 6)
                .background(ScribbleFitColor.softGray)
                .overlay(
                    RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.small)
                        .stroke(ScribbleFitColor.lightGray, lineWidth: 1)
                )
                .clipShape(RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.small))
            }
            .padding(.horizontal, ScribbleFitSpacing.medium)
            .padding(.vertical, ScribbleFitSpacing.medium)

            // Inline secure field — shown when showApiKeyInput is true
            if showApiKeyInput {
                VStack(spacing: ScribbleFitSpacing.small) {
                    SecureField("Enter API key", text: $apiKeyInput)
                        .font(.system(size: 15))
                        .foregroundStyle(ScribbleFitColor.richBlack)
                        .padding(.horizontal, ScribbleFitSpacing.medium)
                        .padding(.vertical, 10)
                        .background(ScribbleFitColor.softGray)
                        .clipShape(RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.small))
                        .padding(.horizontal, ScribbleFitSpacing.medium)

                    Button("Confirm") {
                        Task {
                            await viewModel.onApiKeySaved(apiKeyInput)
                            apiKeyInput = ""
                            showApiKeyInput = false
                        }
                    }
                    .font(.system(size: 15, weight: .semibold))
                    .foregroundStyle(ScribbleFitColor.richBlack)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 10)
                    .background(ScribbleFitColor.softGray)
                    .overlay(
                        RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.small)
                            .stroke(ScribbleFitColor.lightGray, lineWidth: 1)
                    )
                    .clipShape(RoundedRectangle(cornerRadius: ScribbleFitCornerRadius.small))
                    .padding(.horizontal, ScribbleFitSpacing.medium)
                }
                .padding(.bottom, ScribbleFitSpacing.medium)
            }
        }
    }

    // MARK: - Weight Unit Row

    @ViewBuilder
    private func weightUnitRow() -> some View {
        HStack {
            Text("Weight Unit")
                .font(.system(size: 17, weight: .regular))
                .foregroundStyle(ScribbleFitColor.richBlack)

            Spacer()

            // Custom segmented toggle
            HStack(spacing: 0) {
                weightUnitSegment("lbs", unit: .lbs)
                weightUnitSegment("kgs", unit: .kgs)
            }
            .background(ScribbleFitColor.softGray)
            .clipShape(RoundedRectangle(cornerRadius: 6))
        }
        .padding(.horizontal, ScribbleFitSpacing.medium)
        .padding(.vertical, ScribbleFitSpacing.medium)
    }

    @ViewBuilder
    private func weightUnitSegment(_ label: String, unit: Weight) -> some View {
        let isActive = viewModel.uiState.config.weightUnit == unit
        Button {
            Task { await viewModel.onWeightUnitChanged(unit) }
        } label: {
            Text(label)
                .font(.system(size: 13, weight: isActive ? .semibold : .regular))
                .foregroundStyle(isActive ? Color.white : ScribbleFitColor.midGray)
                .padding(.horizontal, 14)
                .padding(.vertical, 6)
                .background(isActive ? ScribbleFitColor.richBlack : Color.clear)
                .clipShape(RoundedRectangle(cornerRadius: 6))
        }
    }

    // MARK: - Display String Helpers

    private func providerDisplayString(_ provider: LLMProvider) -> String {
        switch provider {
        case .proxy: return "Proxy (Default)"
        case .gemini: return "Gemini"
        case .openai: return "OpenAI"
        case .local: return "Local"
        }
    }

    private func modelDisplayString(_ model: String?) -> String {
        guard let model, !model.isEmpty else { return "Auto" }
        return model
    }

    private func themeDisplayString(_ theme: ThemePreference) -> String {
        switch theme {
        case .light: return "Light"
        case .dark: return "Dark"
        case .system: return "System"
        }
    }

    private func maskedApiKey(_ key: String) -> String {
        guard !key.isEmpty else { return "Not set" }
        let suffix = String(key.suffix(4))
        return "•••• \(suffix)"
    }
}
