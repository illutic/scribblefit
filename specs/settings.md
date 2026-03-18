# Feature Specification: Settings (App Configuration)

## 1. Overview
The Settings feature allows users to configure and personalize the ScribbleFit experience. It includes preferences for data units, management of the AI models used for workout parsing, and tools for data export and deletion.

## 2. User Stories
- **As a User**, I want to choose my preferred unit system (Metric vs. Imperial) **so that** my workout data matches my equipment.
- **As a User**, I want to select the AI model used for parsing my scribbles **so that** I can choose the most accurate or cost-effective option for my needs.
- **As a User**, I want to be able to export my data **so that** I can back it up or use it elsewhere.
- **As a User**, I want to be able to delete all my data **so that** I have control over my privacy and information.

## 3. Acceptance Criteria
- [ ] The Settings screen must be accessible via the bottom navigation bar.
- [ ] **AI Model Selection:**
    - [ ] List available models (e.g., Gemini 1.5 Pro/Flash, OpenAI GPT-4o).
    - [ ] Display the currently selected model.
- [ ] **Unit Preferences:**
    - [ ] Toggle or picker for Kilograms (kg) vs. Pounds (lbs).
    - [ ] Update across all relevant screens (Canvas, Ledger, Insights) when changed.
- [ ] **Data Management:**
    - [ ] "Export Data" button (JSON format).
    - [ ] "Clear All Data" button with a confirmation dialog.
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** Title "Settings".
    - [ ] **Body:** Grouped list of settings (Profile, AI Model, Units, Data Management, About).
    - [ ] **Footer:** Bottom navigation bar (shared across the app).

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:settings` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
    - Use `LazyColumn` for settings categories and items.
    - Implement `SettingsHeader`, `SettingsBody`, and `SettingsFooter` as separate contextual Composables.
- **Data Persistence:** Use `DataStore` (Preferences) for app settings and AI configuration.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `SettingsFeature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
    - Use `List` or `Form` for settings.
    - Implement `SettingsHeaderView`, `SettingsBodyView`, and `SettingsFooterView` as separate contextual Views.
- **Data Persistence:** Use `UserDefaults` or a dedicated settings file.
- **Background Tasks:** Swift Concurrency for exporting data.

## 5. Validation
- **Unit Tests:**
    - `SettingsViewModel`/`SettingsStore` state management for toggles and selection.
    - Use Case tests for saving and loading preferences from `DataStore`/`UserDefaults`.
- **Integration Tests:** Verifying data export functionality generates valid JSON.
- **UI Tests:** 
    - Verify that changing a unit in Settings updates the display in the Ledger/Canvas.
    - Verify the "Clear All Data" confirmation dialog works correctly.
