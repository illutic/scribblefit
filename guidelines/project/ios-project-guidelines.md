# ScribbleFit Core Guidelines (iOS)

## 1. Architectural Pattern: Pure SwiftUI MVI (Model-View-Intent)
- **No UIKit:** Specifications MUST design UI solely for native SwiftUI.
- **Store/ViewModel:** Autonomous `@Observable` @MainActor classes.
- **Static Theming:** Use standard SwiftUI `Color` and `Font` extensions for brand consistency. Leverage semantic system colors (e.g., `.primary`, `.secondary`, `.systemBackground`) where possible.
- **Modern Native Parity:** Balance Android parity with iOS-native aesthetics. For iOS 26+, utilize native components like `Tab(role: .search)` and `.searchToolbarBehavior(.minimize)`.
- **Global Tint:** Apply `.tint(.scribblePrimary)` to the root `TabView` and all selection components (e.g., `DatePicker`) to ensure brand consistency.

## 2. Domain & Data Layers (Reactive & SOLID)
- **Swift 6 Concurrency:** Enforce `Sendable` domain models and `@MainActor` stores, repositories, and use cases.
- **Actor-Isolated Mappers:** All `toDomain()` mapping extensions on `SwiftData` entities MUST be marked with `@MainActor` to comply with Swift 6 strict concurrency checks.
- **AI Service Architecture:** Use a `RoutingLLMService` to dynamically switch between `LocalLLMService` and `GeminiLLMService` based on user settings. This ensures logic is decoupled from the provider.
    - **Context Optimization:** AI operations intended for analysis (e.g., generating summaries or trends) MUST receive structured Domain Models (e.g., `[Exercise]`) instead of raw text or unstructured entities (e.g., `Scribble`) to ensure high-quality, reproducible LLM output.
    - **UX Consistency:** Asynchronous AI tasks MUST provide visual feedback via skeleton loading states (e.g., `AIInsightsLoadingView`) to prevent UI "popping" and manage user expectations during inference latency.
    - **Token Optimization:** AI generation triggers MUST be optimized to avoid redundant calls. For date-based features, track the `lastInsightDate` in the Store/ViewModel and only trigger a refresh if the date has changed or if manually forced by the user.
- **Implementation-Specific Use Cases:** Capability checks or configuration-specific logic (e.g., `isSupported()`) MUST NOT be part of a shared domain protocol (e.g., `LLMService`). Instead, create a dedicated Use Case (e.g., `CheckLocalSupportUseCase`) that depends directly on the concrete implementation (e.g., `LocalLLMService`) to keep the shared protocol clean and focused on common functionality.
- **Secure Storage:** API keys and sensitive tokens MUST be stored in the Keychain using a `KeychainHelper`. NEVER use `UserDefaults` for secrets.
- **Reactive Contracts:** Any repository method returning a stream MUST be reactive, ensuring data changes trigger immediate UI updates.
- **Implementation:** Mandate `SwiftData` from the start.
- **Bidirectional Mapping:** All domain models intended for persistence MUST have a corresponding `toEntity()` mapping extension in the Data layer (e.g., in `Mappers.swift`).
- **Atomic Relationship Updates:** When updating a parent entity that owns a list of child relationships (e.g., a `Scribble` with `exercises`), the Repository MUST perform an atomic clear of existing children (using `modelContext.delete`) before re-mapping and assigning the new list. This ensures data integrity and prevents stale or duplicate child records.
- **Codable Parity:** Domain models and entities MUST conform to `Codable` if they are part of data management (export/import).
- **Status Enum Consistency:** Enforce uppercase raw values for status enums (e.g., `case failed = "FAILED"`) to ensure alignment with technical specifications and cross-platform (Android) implementation.
- **Resilient Mapping:** When mapping from storage (String) to Domain (Enum), `toDomain()` mapping MUST use `.uppercased()` on the status string to handle case-insensitive database values safely.

## 3. UI & Design System (DRY)
- **Native Navigation:** Prefer native `ToolbarItem` placements (`.principal`, `.topBarTrailing`) over custom `HeaderView` components.
- **Persistent Input Bars:** When implementing a floating input bar above a `TabView`, use a `VStack` or `.safeAreaInset(edge: .bottom)` to ensure correct stacking and visibility across all iOS versions (17+).
- **Settings Interaction:** Use native `.segmented` pickers for toggle-like configurations (Theme, Units, Providers) to maintain system consistency.
- **Theme Reactivity:** Apply `.preferredColorScheme` to BOTH the root `ContentView` AND any presented modals (e.g., `fullScreenCover`) to ensure theme changes apply instantly throughout the app hierarchy.
- **Localization:** Use `Localizable.xcstrings` for all UI text.
- **Contextual Splitting:** Views MUST be split into contextual components (e.g., `BodyView`, `ScribbleInputBar`).

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent SPM targets.
- **Project Integrity:** When adding new Swift files, MANDATORY verification of the `ScribbleFit.xcodeproj` project file is required. Ensure the file is listed in both the `PBXFileReference` and the correct `PBXSourcesBuildPhase` to avoid "undefined symbol" or "invalid argument" errors at launch.
- **Target Versions:** Support iOS 17.0+ while leveraging iOS 26 (18+) native enhancements via `#available` checks.
