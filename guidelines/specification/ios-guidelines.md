# iOS Implementation Guidelines (spec-implementer)

## Core Principles
- **SOLID:** Use Cases (SRP), Protocol-based Repositories (DIP/OCP).
- **DRY:** Reuse `CoreDesignSystem` components.
- **KISS:** Keep Views and Use Cases simple and focused.
- **Autonomous Modularity:** Targets MUST be independent; share logic only via `Core` targets.

## Architecture: Pure SwiftUI MVI (Strictly NO UIKit)

### 1. ViewModel (Store) Structure
Each Store must be autonomous using `@Observable` and must be isolated to `@MainActor`.
- **Zero Business Logic:** Stores only orchestrate UI state via Use Cases.
- **State:** A `struct` representing the UI state.
- **Reactive Observation:** Ensure `observe` tasks are cancelled and restarted when key state (like dates) change.
- **No UIKit:** Absolutely no `import UIKit`. Use `UnevenRoundedRectangle` for custom corners.

### 2. Domain Layer (The Brain)
- **Models:** Simple `struct` types. All domain models MUST conform to `Sendable`.
- **Codable Parity:** Models intended for JSON export MUST conform to `Codable`.
- **Repository Protocol:** Define the contract here. Stream-based getters MUST be reactive.
- **Use Cases:** Explicitly isolated to `@MainActor` to match the Store and Repository, or conform to `Sendable`.

### 3. Data Layer (The Implementation)
- **Reactive Repositories:** Use `Combine.PassthroughSubject` or `Observation` to signal data changes to open `AsyncStream` cursors. This is critical for real-time UI updates.
- **SwiftData Concurrency:** Handling `ModelContext` requires strict actor boundaries. Use `@preconcurrency import Combine` where necessary.
- **Secure Storage:** Use a Keychain wrapper (e.g., `KeychainHelper`) for sensitive data (API keys, tokens).
- **Model Mapping:** Centralize `toDomain()` and `toEntity()` mapping within the Data layer (e.g., `Mappers.swift`).
    - **Status Enum Consistency:** Enforce uppercase raw values for status enums (e.g., `case failed = "FAILED"`) to match specifications and Android implementation.
    - **Resilient Mapping:** `toDomain()` mapping MUST use `status.uppercased()` when mapping from String to Enum to handle case-insensitive database values.
    - **Bidirectional Support:** Ensure all domain models intended for persistence have a corresponding `toEntity()` mapping.
- **AI Integration:** For AI-driven features (parsing, summaries), integrate with equivalent LLM services via async/await. Define protocols like `LLMService` in the domain for decoupled integration.
- **No Mocks in Production:** Configure real `SwiftData` containers in `App.swift` immediately.

## 4. UI Layer (The View)
- **Design System:** Use `CoreDesignSystem` tokens.
    - **Semantic Colors:** Use brand-defined semantic colors (e.g., `Color.scribbleDanger`, `Color.scribbleSuccess`) for states like error, failure, or completion instead of standard system colors (e.g., `Color.red`).
    - **Missing Data Feedback:** When displaying critical data that is expected but may be missing (e.g., AI-parsed results), the View MUST provide a clear empty state message (e.g., "No exercises parsed from your scribble.") and an appropriate icon (e.g., `exclamationmark.triangle`).
- **Localization:** Use `Localizable.xcstrings` for all UI text.
    - **Strict State Resolution:** All UI strings (labels, hints, placeholders, and formatted messages) MUST be resolved in the `State` or `Store`.
    - **View Isolation:** Contextual components (like `HeaderView`, `BodyView`, `FooterView`) MUST NOT call `String(localized:)` directly. They should receive pre-resolved `String` or `LocalizedStringKey` parameters.
    - **Formatting:** Formatting logic (e.g., `String(localized: "format", ...)`) MUST be encapsulated in the `State` or `Store`.
- **Data Export:** Use the `Transferable` protocol via a dedicated wrapper struct (e.g., `ExportFile`) for sharing data.
- **Contextual Splitting:** Views MUST be split into contextual components (e.g., `HeaderView`, `BodyView`, `FooterView`).
- **Component Isolation:** Each major contextual area should be implemented as a separate SwiftUI `View` to ensure focus and testability.
- **Static Theming:** Use standard SwiftUI `Color` and `Font` extensions that automatically adapt to light/dark mode. Avoid complex `EnvironmentKey` runtime mappings for basic brand colors.
- **Theme Concurrency:** All static color and font extensions MUST be `Sendable`.
- **Native OS Aesthetics (iOS 26+):** Use the refined glass pattern: `.background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))`. This ensures the material correctly conforms to the container shape and avoids rectangular artifacts.
- **Platform Parity:** Maintain brand parity. Use native modifiers to enhance the OS-specific feel.

## Committing Workflow
1. `feat(domain): [feature] models and logic`
2. `feat(core): [feature] shared infra`
3. `feat(data): [feature] reactive implementation`
4. `feat(ai): [feature] AI integration and logic`
5. `feat(ui): [feature] MVI screens with static theming and native glass effects`
