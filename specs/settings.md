# Feature Specification: Settings (App Configuration)

## 1. Overview
The Settings feature allows users to configure and personalize the ScribbleFit experience. It manages aesthetic preferences, AI model selection, data privacy, and portable data export. The UI follows the **"Editorial Minimalism"** (Digital Atelier) design system, focusing on tonal layering, high-end typography, and aggressive white space.

## 2. User Stories
- **As a User**, I want to choose between **Light, Dark, or System themes** **so that** the app matches my visual preference.
- **As a User**, I want to choose my preferred **unit system (Metric vs. Imperial)** **so that** my training data matches my equipment.
- **As a User**, I want to select between **Cloud (Gemini) or Local AI models** for parsing my scribbles **so that** I can balance accuracy and privacy.
- **As a User**, I want to **securely enter my API Key** for cloud models **so that** the app can access advanced AI features.
- **As a User**, I want to **export my entire training history as JSON** **so that** I have a portable backup of my data.
- **As a User**, I want to **delete all my data (Scribbles, Exercises, Sets)** **so that** I can reset my progress or protect my privacy.

## 3. Acceptance Criteria

### 3.1 Design Alignment (Editorial Minimalism)
- [ ] **No-Line Rule:** Zero 1px solid borders for sectioning. Use background color shifts (`surface` to `surface-container-low`) and white space for separation.
- [ ] **Tonal Layering:**
    - Base Background: `surface` (#F9F9F9).
    - Section Grouping: `surface-container-low` (#F3F3F4).
    - Interactive Elements/Cards: `surface-container-lowest` (#FFFFFF).
- [ ] **Typography:** Use **Inter**. Section headers must be `Headline-SM` (1.5rem, Semi-Bold, -0.02em tracking). Labels for inputs must be `Label-MD` (0.75rem, Semi-Bold, +0.05em tracking, Uppercase).
- [ ] **Interactive Elements:** Buttons and switches must follow the pill shape (`9999px`).

### 3.2 Contextual UI Splitting
#### **Header**
- [ ] **Title:** "Settings" using `Headline-SM` (or `Display-LG` for hero impact as per DESIGN.md).
- [ ] **Back Button:** A ghost-style pill button or a minimalist thin-stroke arrow icon to return to the previous screen.

#### **Body (Grouped Sections)**
- [ ] **Appearance:**
    - [ ] Selector for Theme: Light, Dark, System. Uses `surface-container-lowest` for the selection background.
    - [ ] Immediate UI update across all active screens upon change.
- [ ] **AI Configuration:**
    - [ ] **Provider Selection:** Toggle between "Cloud" and "Local".
    - [ ] **Cloud Settings (Conditional):** Visible only when "Cloud" is selected.
        - [ ] **API Key Input:** Fully rounded pill with `surface-container` (#EEEEEE) background. "Hide/Show" toggle with a thin-stroke eye icon.
        - [ ] **Test Connection:** A primary pill button (#000000) to verify the key. Displays feedback states: "Testing...", "Success", or "Error".
        - [ ] **Model Selection:** Dropdown/Selector for Gemini models (e.g., Gemini 1.5 Pro, Flash). Only enabled if API key is validated.
- [ ] **Unit Preferences:**
    - [ ] Segmented control or toggle for Kilograms (kg) vs. Pounds (lbs).
    - [ ] Consistent application across Canvas, Ledger, and Insights.
- [ ] **Data Management:**
    - [ ] **Export Data:** Secondary button style (no border, `surface-container-high`). Triggers system share sheet with a `.json` file.
    - [ ] **Clear All Data:** Tertiary/Danger button style. Triggers a tonal-layered confirmation dialog with a warning message.

#### **Footer**
- [ ] Displays App Version and Build Number using `Label-MD` typography in `mid-gray`.
- [ ] Links to "Privacy Policy" and "Terms of Service" as ghost-style tertiary actions.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:settings` with sub-modules `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`. 
    - No `Divider()` components. Use `Spacer` and background colors.
    - String resolution in `SettingsState` via `@Composable @ReadOnlyComposable` getters.
- **Data Persistence:** 
    - Bridge to `:core:config:domain`'s `ConfigRepository`.
    - Use `SecureKeyStorage` for API Keys.
- **Dependency Injection:** Hilt. Use Cases provided via a Hilt `@Module` in the `:data` layer.
- **AI Integration:** For connection testing and model fetching, integrate with `:feature:ai:domain`'s `LLMEngine`.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `SettingsFeature`.
- **UI:** 100% SwiftUI.
    - Use `Section` with custom headers (no borders).
    - Apply `.glassEffect(.regular.interactive())` to floating overlays or modals.
- **Data Persistence:** 
    - `ConfigRepository` implementation using `SwiftData`.
    - **Keychain** wrapper for secure API key storage.
- **Concurrency:** Swift 6 `Sendable` models and `@MainActor` stores.
- **Data Export:** `Transferable` protocol for JSON export.

## 5. Validation
- **Unit Tests:**
    - `SettingsViewModel`/`SettingsStore` for state transitions (e.g., toggling AI provider visibility).
    - `ExportUserDataUseCase` generates a schema-valid JSON (verified via `kotlinx.serialization`).
- **Integration Tests:** 
    - Verify `ClearUserDataUseCase` resets all Room/SwiftData tables.
    - Verify API Key persistence in `SecureKeyStorage`/`Keychain`.
- **UI Tests:** 
    - Snapshot tests for Light and Dark modes.
    - Verify "Test Connection" feedback states (Loading, Success, Error).
