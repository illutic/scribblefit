# Feature Specification: Settings (App Configuration)

**Feature Branch**: `[feature-settings]`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "The Settings feature allows users to configure and personalize the ScribbleFit experience. It manages aesthetic preferences, AI model selection, data privacy, and portable data export. The UI follows the 'Editorial Minimalism' (Digital Atelier) design system."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Configure App Appearance (Theme & Units) (Priority: P1)

As a User, I want to choose between Light, Dark, or System themes and select my preferred unit system (Metric vs. Imperial) so that the app matches my visual preference and my training equipment.

**Why this priority**: Essential for basic user personalization and usability. Core part of any app configuration.

**Independent Test**: Can be fully tested by selecting different themes and verifying UI changes instantly, and by changing unit systems and observing changes in Canvas, Ledger, and Insights.

**Acceptance Scenarios**:

1. **Given** the app is running, **When** I change the theme to "Dark", **Then** the UI updates to use the dark theme across all active screens.
2. **Given** the app is running, **When** I toggle the unit preference to "Kilograms (kg)", **Then** this unit system is consistently applied across Canvas, Ledger, and Insights.

---

### User Story 2 - Configure AI Provider and API Key (Priority: P2)

As a User, I want to select between Cloud (Gemini) or Local AI models for parsing my scribbles and securely enter my API Key for cloud models so that I can balance accuracy and privacy while accessing advanced AI features.

**Why this priority**: Central to the app's AI functionality, but secondary to basic visual preferences.

**Independent Test**: Can be tested by switching between Local and Cloud, entering an API key, testing the connection, and verifying the key is securely stored.

**Acceptance Scenarios**:

1. **Given** the AI configuration section, **When** I select "Cloud", **Then** the API Key input and model selection UI becomes visible.
2. **Given** I have entered a cloud API key, **When** I click "Test Connection", **Then** the app verifies the key and displays feedback states ("Testing...", "Success", or "Error").
3. **Given** an API key is validated, **When** the connection is successful, **Then** the Model Selection dropdown (e.g., Gemini 1.5 Pro, Flash) is enabled.

---

### User Story 3 - Data Management (Export and Deletion) (Priority: P3)

As a User, I want to export my entire training history as JSON and have the ability to delete all my data (Scribbles, Exercises, Sets) so that I have a portable backup and can reset progress or protect privacy.

**Why this priority**: Important for data ownership and privacy but not required for primary usage of the app.

**Independent Test**: Can be tested by pressing "Export Data" to generate a valid JSON file, and pressing "Clear All Data" to verify data is deleted.

**Acceptance Scenarios**:

1. **Given** the settings screen, **When** I click "Export Data", **Then** the system share sheet opens with a schema-valid `.json` file containing my training history.
2. **Given** the settings screen, **When** I click "Clear All Data", **Then** a confirmation dialog appears with a warning message.
3. **Given** the confirmation dialog is open, **When** I confirm deletion, **Then** all my data (Scribbles, Exercises, Sets) is permanently removed from the system.

### Edge Cases

- What happens when the AI API key is invalid or the connection times out during a test?
- How does system handle data export if the user has no training data yet?
- What happens when a user attempts to change the unit system while a workout is actively being logged in the background?
- How does the system respond if the theme is changed rapidly multiple times?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST allow users to select between Light, Dark, or System themes, immediately updating UI across active screens.
- **FR-002**: System MUST allow users to toggle between Metric (kg) and Imperial (lbs) unit systems, applying this globally across the app.
- **FR-003**: System MUST allow users to toggle AI provider between "Cloud" and "Local".
- **FR-004**: System MUST securely store the Cloud API Key using `SecureKeyStorage` (Android) or `Keychain` (iOS).
- **FR-005**: System MUST provide a "Test Connection" functionality for the Cloud API Key and display status (Loading, Success, Error).
- **FR-006**: System MUST only enable model selection (e.g., Gemini 1.5 Pro, Flash) if the API key is successfully validated.
- **FR-007**: System MUST allow users to export all training data as a schema-valid `.json` file via the system share sheet.
- **FR-008**: System MUST allow users to clear all user data with a confirmation warning dialog.
- **FR-009**: The UI MUST follow the "Editorial Minimalism" design guidelines: no solid borders, tonal layering, Inter typography.
- **FR-010**: System MUST display App Version and Build Number, and link to Privacy Policy and Terms of Service.

### Key Entities *(include if feature involves data)*

- **ConfigRepository**: Represents the local storage mechanism for app preferences (Theme, Units, AI Provider selection).
- **SecureKeyStorage/Keychain**: Secure storage entity strictly used for API Keys.
- **UserData**: Represents the aggregated JSON payload of a user's entire history (Scribbles, Exercises, Sets) exported.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Theme and unit configuration preferences are successfully saved and persistently re-loaded upon app restarts.
- **SC-002**: Users can successfully authenticate their Gemini Cloud API key, with clear success/failure feedback in the UI.
- **SC-003**: Data export produces a correctly formatted JSON document that includes all requested entities without causing memory overflows.
- **SC-004**: Data clearing operation completely resets the local databases on both Android (Room) and iOS (SwiftData), leaving zero lingering records.
- **SC-005**: All UI tests pass, validating "Editorial Minimalism" compliance including Snapshot tests for Light/Dark modes.

## Assumptions

- Users have basic understanding of what an API key is and how to retrieve it from Google AI Studio.
- The exported JSON schema is well-defined and backwards-compatible with potential future import features.
- Local AI model is available or will handle missing models gracefully.
- The UI uses Jetpack Compose on Android and SwiftUI on iOS, adopting strict MVI architectures as outlined in project guidelines.
