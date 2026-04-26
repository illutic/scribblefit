# ScribbleFit Core Guidelines (iOS)

## 1. Architectural Pattern: Pure SwiftUI MVI (Model-View-Intent)
- **No UIKit:** Specifications MUST design UI solely for native SwiftUI.
- **Store/ViewModel:** Autonomous `@Observable` @MainActor classes.
- **Task Serialization:** Stores MUST ensure that asynchronous tasks affecting the same state branch are processed sequentially where order matters (e.g., adding a set then refreshing a summary). Use `Task` groups or serial execution patterns to prevent race conditions during rapid user input.
- **Static Theming:** Use standard SwiftUI `Color` and `Font` extensions for brand consistency. Leverage semantic system colors (e.g., `.primary`, `.secondary`, `.systemBackground`) where possible.
- **Best-in-Class Defaults (Editorial Minimalism):** Prefer hardcoding optimal defaults (e.g., AI model names like `gemini-2.5-flash-lite`) over adding complex, database-backed configuration settings. Only expose settings that provide significant user value to minimize state management overhead and schema complexity.
- **Modern Native Parity:** Balance Android parity with iOS-native aesthetics. For iOS 26+, utilize native components like `Tab(role: .search)` and `.searchToolbarBehavior(.minimize)`.
- **Global Tint:** Apply `.tint(.scribblePrimary)` to the root `TabView` and all selection components (e.g., `DatePicker`) to ensure brand consistency.

## 2. Domain & Data Layers (Reactive & SOLID)
- **Swift 6 Concurrency:** Enforce `Sendable` domain models and `@MainActor` stores, repositories, and use cases.
- **Actor-Isolated Mappers:** All `toDomain()` mapping extensions on `SwiftData` entities MUST be marked with `@MainActor` to comply with Swift 6 strict concurrency checks.
- **Reactive Use Cases:** If a Use Case returns a data stream, it MUST return an `AsyncStream` and properly map the underlying repository's reactive stream to the domain model.
- **Cross-Feature Dependencies:** Use Cases, Views, and Components intended for cross-feature consumption MUST be marked `public` (including their explicit initializers) to ensure visibility across SPM targets. Swift's default memberwise initializer is `internal` and will cause build errors in multi-module setups.
- **AI Service Architecture:** Use the `FirebaseAI` SDK (v12.0.0+) and a `RoutingLLMService` to dynamically switch between `LocalLLMService` and `GeminiLLMService` based on user settings. This ensures logic is decoupled from the provider.
    - **Initializer Consistency:** Service initializers MUST maintain a consistent argument order. When adding new dependencies (e.g., to `RoutingLLMService`), ensure the call site in the Data layer matches the definition exactly.
    - **Dynamic Initialization:** LLM model instances MUST be initialized dynamically (e.g., via a getter or helper function) rather than stored as long-lived properties. This ensures they always pick up the latest reactive configuration (e.g., model name changes in `SystemConfig`) for each request.
    - **JSON Sanitization:** AI responses intended for parsing MUST be sanitized to remove potential Markdown code block markers (e.g., ` ```json ... ``` `) before decoding.
    - **Backend Targeting:** For the free tier/direct Gemini access, explicitly specify the `.googleAI()` backend in the SDK initialization to distinguish it from Vertex AI on Google Cloud.
    - **Context Optimization:** AI operations intended for analysis (e.g., generating summaries or trends) MUST receive structured Domain Models (e.g., `[Exercise]`) instead of raw text or unstructured entities (e.g., `Scribble`) to ensure high-quality, reproducible LLM output.
    - **UX Consistency:** Asynchronous AI tasks MUST provide visual feedback via skeleton loading states (e.g., `AIInsightsLoadingView`) to prevent UI "popping" and manage user expectations during inference latency.
    - **Token Optimization:** AI generation triggers MUST be optimized to avoid redundant calls. For date-based features, track the `lastInsightDate` in the Store/ViewModel and only trigger a refresh if the date has changed or if manually forced by the user.
- **Implementation-Specific Use Cases:** Capability checks or configuration-specific logic (e.g., `isSupported()`) MUST NOT be part of a shared domain protocol (e.g., `LLMService`). Instead, create a dedicated Use Case (e.g., `CheckLocalSupportUseCase`) that depends directly on the concrete implementation (e.g., `LocalLLMService`) to keep the shared protocol clean and focused on common functionality.
- **Secure Storage:** API keys and sensitive tokens MUST be stored in the Keychain using a `KeychainHelper`. NEVER use `UserDefaults` for secrets.
- **Reactive Contracts:** Any repository method returning a stream MUST be reactive (e.g., `AsyncStream`), ensuring data changes trigger immediate UI updates.
- **Parallel Data Fetching & Non-blocking Observation:** When a Store needs to observe multiple independent data sources, parallelize the observation (e.g., using separate `Task`s or `withTaskGroup`). Do NOT wait for all streams to emit if the UI can partially render. Set `isLoading = false` upon the first emission of the primary data source (e.g., the main stats or frequency) to ensure the UI becomes interactive as soon as possible.
- **Store-Level Debouncing:** For expensive side effects (like AI generation) triggered by data changes, implement debouncing using a `Combine` `PassthroughSubject` or `Task.sleep` to prevent redundant calls.
- **Prompt Parity:** AI prompts and response models MUST be strictly aligned with the Android implementation. Derived or redundant fields (e.g., `improvement`) MUST be removed to maintain cross-platform logic consistency.
- **Implementation:** Mandate `SwiftData` from the start.
- **Entity Naming Parity:** `SwiftData` entity names MUST strictly mirror their Domain model equivalents (e.g., `Exercise` -> `ExerciseEntity`) to prevent "entity mismatch" bugs during migration or mapping.
- **Bidirectional Mapping:** All domain models intended for persistence MUST have a corresponding `toEntity()` mapping extension in the Data layer (e.g., in `Mappers.swift`).
- **Object Identity Synchronization:** Every `save` or `insert` operation MUST perform a lookup by ID before creating a new entity. If an entity with the same ID exists in the `ModelContext`, it MUST be updated in place. This prevents duplicates and maintains the integrity of the object graph.
- **Intelligent Synchronization Pattern:** When updating a `Scribble` entity that owns `Exercise` and `Set` records, the Repository MUST perform in-place synchronization instead of a blind "clear and re-insert". 
    1. **Iterative Update:** Match existing entities by ID. Update their properties in place instead of replacing the entire object.
    2. **Recursive Sync:** Apply the same logic down the object graph (e.g., from `Exercise` to its `Sets`).
    3. **Avoid Flickering:** This pattern prevents UI flickering in reactive streams and ensures data integrity for shared entities.
- **Codable Parity:** Domain models and entities MUST conform to `Codable` if they are part of data management (export/import).
- **Status Enum Consistency:** Enforce uppercase raw values for status enums (e.g., `case failed = "FAILED"`) to ensure alignment with technical specifications and cross-platform (Android) implementation.
- **Resilient Mapping:** When mapping from storage (String) to Domain (Enum), `toDomain()` mapping MUST use `.uppercased()` on the status string to handle case-insensitive database values safely.
- **End-to-End Nullability:** Maintain nullability parity across all layers (Store -> Use Case -> Repository -> SwiftData). If a value can be cleared, the underlying schema and repository methods MUST support `Optional` types.
- **Explicit Numeric Casting:** Avoid relying on implicit conversion for optional numeric types. When assigning an `Int` to a `Float?`, use `Float(value)`. For literals, use the correct suffix or decimal point (e.g., `0.0` for `Float`).
- **Reactive Formatting Use Cases:** Complex formatting logic that requires business rules (e.g., grouping sets) or depends on global configuration (e.g., Units, Locale) MUST be implemented as a Domain Use Case. Stores MUST orchestrate the combination of the primary data stream and the configuration stream before passing the values to the Use Case to ensure UI reactivity.
- **Pure State Enforcement:** `State` structs MUST remain pure data containers. 
    - **No Logic Orchestration:** Use Cases MUST NOT be instantiated or orchestrated within the `State` struct or `Store` initializer. 
    - **UI Model Projection:** Stores MUST NOT pass raw Domain Models (e.g., `Exercise`) directly to the `State`. They MUST project them into `UiModel` structs that contain pre-resolved strings, visibility flags, and formatted values. This prevents business logic leakage into SwiftUI `if/else` blocks.
- **Mutable State for Bindings:** Any property in a `State` struct that is bound to a UI control (e.g., `TextField`, `Toggle`, `Picker`) MUST be declared as `var` to support SwiftUI's two-way bindings.

## 3. UI & Design System (DRY)
- **Native Navigation:** Prefer native `ToolbarItem` placements (`.principal`, `.topBarTrailing`) over custom `HeaderView` components.
- **Navigation Abstraction:** Views SHOULD prefer closure-based navigation (e.g., `onNavigate: (UUID) -> Void`) over injecting child stores for navigation purposes, keeping the view focused on presentation.
- **Decentralized Navigation (Sheet Ownership):** Detail sheets (e.g., `ExerciseDetailsView`) MUST be owned and presented by their parent feature view (e.g., `LedgerView`) rather than the root `ContentView`. This local ownership prevents unnecessary root state re-evaluations and ensures smooth dismissal transitions without UI "flicker".
- **Editorial Minimalism:**
    - **Zero-States:** Handle optional or null numeric values with human-friendly labels (e.g., "Bodyweight" for a null `weight` in an exercise summary).
    - **Visual Hierarchy:** Use brand-defined semantic colors and weight (e.g., `Color.scribblePrimary`) to guide user attention.
- **Nullable Numeric Inputs:**
    - **State Initialization:** When mapping domain models to view state for editing, handle null numeric values by providing default strings (e.g., `weight.map(String.init) ?? "0"`).
    - **Empty String Support:** Input binding logic MUST treat empty strings as `nil` if the domain field is optional, ensuring the ability to "clear" values in the database.
- **Persistent Input Bars:** When implementing a floating input bar above a `TabView`, use a `VStack` or `.safeAreaInset(edge: .bottom)` to ensure correct stacking and visibility across all iOS versions (17+).
- **Settings Interaction:** Use native `.segmented` pickers for toggle-like configurations (Theme, Units, Providers) to maintain system consistency.
- **Theme Reactivity:** Apply `.preferredColorScheme` to BOTH the root `ContentView` AND any presented modals (e.g., `fullScreenCover`) to ensure theme changes apply instantly throughout the app hierarchy.
- **Single-Component File Pattern:**
    - **One File, One View:** Every `View` struct MUST reside in its own dedicated Swift file. Avoid "dumping" multiple sub-component structs or private helper views into a single file.
    - **Organization:** Store sub-components in a `Components/` sub-directory within the feature's `UI/` folder.
    - **Main View Minimalism:** Feature main views (e.g., `CanvasView`, `SettingsView`) MUST only contain high-level layout and navigation logic, delegating all section and item rendering to individual component files.
    - **Exception:** Very small, non-reusable layout helpers (< 15 lines) that do not contain business logic MAY remain in the parent file, but extraction is always preferred.
- **Contextual Splitting:** Views MUST be split into contextual components (e.g., `BodyView`, `ScribbleInputBar`).
- **View Safety:** Contextual components MUST safely unwrap optional state values before rendering. Use `if let` or `map` logic to ensure UI components receive non-optional data.
- **Glassmorphism:** Prefer native SwiftUI modifiers (e.g., `.background(.ultraThinMaterial, in: RoundedRectangle(cornerRadius: 16))`) over custom view extensions to avoid naming ambiguity and ensure long-term compatibility.
- **Genericism:** UI components in shared design libraries MUST be strictly agnostic of business logic and navigation entities. They must use generic UI-specific data models (e.g., `Image`, `String`) to ensure maximum reusability.
- **Navigation UI Mapping:** The responsibility for mapping navigation destinations (e.g., `Screen`) to their UI representation (icons, localized labels) resides in the feature UI layer consuming the navigation component.
- **Localization Choice:** Use `Localizable.xcstrings` for all UI text. Navigation labels and feature-specific strings MUST reside in the feature module's localization files to maintain encapsulation.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent SPM targets.
- **Module Discipline:** Adding a new feature requires (1) defining the target in `Package.swift`, (2) adding dependencies to consuming targets, and (3) updating imports in the Store.
- **Error Handling:** Use Cases and Stores MUST return domain-specific error types via `Result` or custom `Error` enums (e.g., `ScribbleError` in the domain layer) instead of relying on generic system errors. This ensures predictable error propagation through the MVI state.
- **Debounced Persistence:** For Canvas-style editing or live-updating drafts, the Store MUST implement debounced persistence. Use a `Task` with `try await Task.sleep(for: .milliseconds(500))` that is cancelled and restarted on every user input. Always check `if Task.isCancelled { return }` after the sleep to prevent redundant database I/O.
- **Prompt Realism:** LLM prompt instructions MUST be logically consistent with the context provided. Do not instruct the AI to reference historical data or "previous" context if that data is not explicitly included in the inference payload.
- **Target Versions:** Support iOS 17.0+ while leveraging iOS 26 (18+) native enhancements via `#available` checks.

## 5. Project Structure & Modularity (Clean Root Pattern)
- **Hybrid Resolution:** To resolve ambiguity between the main App target and local Swift Package Manager (SPM) targets, the project follows the **Clean Root Pattern**:
    - **`Sources/`**: Reserved exclusively for the main App target (e.g., `ScribbleFitApp.swift`, `GoogleService-Info.plist`).
    - **`LocalPackages/`**: Dedicated directory for all internal SPM modules, further organized into architectural sub-layers:
        - `LocalPackages/Core/`: Shared logic, database, design system, and models.
        - `LocalPackages/Features/`: Domain and UI for specific app features.
- **Path Specification:** In `Package.swift`, every target MUST explicitly specify its `path` pointing to the corresponding subfolder within `LocalPackages/`.
- **Project Integrity:** When adding new Swift files to the main App target, MANDATORY verification of the `ScribbleFit.xcodeproj` project file is required. Ensure the file is listed in both the `PBXFileReference` and the correct `PBXSourcesBuildPhase`. SPM targets do NOT require `.xcodeproj` updates as they are managed by `Package.swift`.
- **Bidirectional Localization:** Navigation labels and feature-specific strings MUST reside in the feature module's `Localizable.xcstrings`. Global strings reside in the main App's resources.

## 6. Daily History Grouping
- **Goal:** Aggregation of multiple discrete logs (scribbles) into a single date-based view item for history-based features (e.g., Ledger).
- **State-Managed Aggregation:** Aggregation MUST NOT happen in the Repository or Use Case. It MUST live in the UI `State` struct as a derived property.
- **Flattened Hierarchy:** All child records (e.g., `Exercise`) from all scribbles on a given date MUST be flattened into a single list within a `GroupedScribbles` (or similar) UI struct.
- **Sorting Consistency:**
    - **Days:** The main collection MUST be sorted by date descending (newest days first).
    - **Intra-day:** Within each day, scribbles or exercises SHOULD be ordered chronologically (ascending by time).
- **UI Representation:** Each day MUST be represented by a single UI container (e.g., a Card with `.background(.ultraThinMaterial)`) containing a date header and the combined list of all activities for that day, regardless of how many individual sessions were recorded.

## 7. History & Time Integrity
- **Rolling History Pattern:** Features displaying historical data (e.g., Ledger, Insights) MUST default to a "Rolling 30-Day Window". This ensures the UI remains focused on recent, relevant activity while providing a consistent starting point across features.
- **Local-First Time Policy:** Avoid manual UTC normalization or Unix timestamp arithmetic. All date-based queries MUST use local calendar ranges to define boundaries.
    - **Query Start:** Use `Calendar.current.startOfDay(for: date)`.
    - **Query End:** Use `Calendar.current.date(byAdding: .day, value: 1, to: startOfDay)!`.
    - **Predicate:** Use `#Predicate` with `scribble.date >= rangeStart && scribble.date < rangeEnd`.
    This policy is MANDATORY for all features dealing with aggregations (e.g., Insights, Frequency counts), ensuring data integrity regardless of the user's current timezone and preventing "missing history" bugs caused by UTC shifts.

## 8. Data Visualization (Canvas Charting)
- **Sequential Charting Pattern:** To ensure cross-platform parity and deterministic layering when using a primitive SwiftUI `Canvas`, all charts MUST be rendered in the following strict order:
    1.  **Infrastructure (Y-Axis):** Draw axis labels and grid lines first to establish the vertical scale.
    2.  **Atmosphere (Fill):** Draw area gradients next.
    3.  **Skeleton (Path):** Draw the trend line (e.g., using cubic Bézier curves) over the fill.
    4.  **Highlights (Points):** Draw data points using the "Halo Point Effect" to ensure they "sit" on top of the line.
    5.  **Context (X-Axis):** Draw time-based labels last.
- **Halo Point Effect:** To create visual separation between a data point and the trend line without using platform-specific shadows:
    1.  **The Halo:** Draw a larger circle (radius `R1`) filled with the chart's background color (e.g., `.scribbleSurfaceContainerLow`).
    2.  **The Point:** Draw a smaller circle (radius `R2 < R1`) in the primary data color centered on the same point.
    3.  This pattern effectively "cuts out" the line behind the point, ensuring maximum legibility.

## 9. Remote Configuration Pattern
- **Task-Based Merging:** Use `AsyncStream` or `CurrentValueSubject` to merge local persistence (e.g., `UserDefaults`, `SwiftData`) with remote configuration (e.g., Firebase Remote Config) in the `ConfigRepository`. Perform the remote fetch asynchronously and update the subject/stream to notify the UI.
- **Ephemeral Storage Rule:** Do NOT persist remote configurations (like AI prompts or feature flags) in the local database or `UserDefaults` if they are managed by an external cloud provider. Remote properties should be stored as `@Transient` or simply non-persistent properties in the Domain Model (e.g., `SystemConfig.remoteConfig`).
- **Architecture Consolidation:** Avoid fragmented configuration logic. If multiple sources (Local, Firebase, Defaults) contribute to a single domain entity, consolidate the logic into a single modular implementation in `FeatureConfig` (or equivalent) to maintain a strict source of truth.
