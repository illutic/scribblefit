# ScribbleFit Core Guidelines (Android)

## 1. Architectural Pattern: MVI (Model-View-Intent)
- **ViewModel:** Autonomous classes (no base inheritance).
- **State:** Immutable `data class`. String resolution MUST happen here via `@Composable @ReadOnlyComposable` getters.
- **State Flow Uncoupling:** ViewModels MUST use a private `MutableStateFlow` to manage the backing state. This flow is updated by various logic workers (reactive collectors, async launchers, intents) and then exposed as a clean, public `StateFlow` via `asStateFlow()`. This "state-sink" approach prevents UI components from directly modifying state and allows multiple concurrent asynchronous operations (e.g., loading AI insights in parallel with primary metrics) to update the UI state predictably without complex reactive chaining.
- **Intent:** `sealed interface` for user actions.
- **Business Logic:** Zero logic in ViewModel; all logic resides in Use Cases (SRP).
- **Best-in-Class Defaults (Editorial Minimalism):** Prefer hardcoding optimal defaults (e.g., AI model names like `gemini-2.5-flash-lite`) over adding complex, database-backed configuration settings. Only expose settings that provide significant user value to minimize state management overhead and schema complexity.

## 2. Domain & Data Layers (SOLID)
- **Repository Interface:** Defined in `:domain` (Dependency Inversion).
- **Atomic Fetching:** Repository methods intended for UI lists MUST return the full nested hierarchy (e.g., using Room's `@Relation`) in a single `@Transaction` to ensure UI consistency. If a single-query hierarchy is not feasible due to complexity or dynamic relationships, the Use Case MUST orchestrate enrichment using `flatMapLatest` and `combine` to ensure an atomic emission of the final list.
- **Database Schema Evolution:** Use Room's `autoMigrations` for simple schema changes such as adding or removing columns. Complex migrations (e.g., table splits) MUST be handled via manual `Migration` classes and thoroughly tested.
- **Implementation-Specific Use Cases:** Capability checks or configuration-specific logic (e.g., `isLocalSupported()`) MUST NOT be part of a shared domain interface (e.g., `LLMEngine`). Instead, create a dedicated Use Case (e.g., `CheckLocalSupportUseCase`) that depends directly on the concrete implementation (e.g., `LocalAIEngine`) to keep the shared interface clean and focused on common functionality.
- **Implementation:** Defined in `:data` (Interface based).
- **Use Cases:**
    - **Single Responsibility:** The *only* place for business logic.
    - **Reactive Mastery:** Use Cases that depend on a `Flow` of parameters (e.g., a date flow from the UI) MUST use `flatMapLatest` to ensure only the most recent parameter's stream is active.
    - **Reactive Enrichment:** When deep child relations (e.g., Exercise -> Sets) are not included in the base list query, Use Cases MUST orchestrate enrichment using `flatMapLatest` and `combine` to ensure a single, consistent state emission.
    - **Non-blocking Flow Observation:** In ViewModels, parallelize the observation of independent data sources (e.g., using `combine` or separate `launch` blocks). Do NOT wait for all flows to emit if the UI can render partially. Set `isLoading = false` as soon as the primary data trigger (e.g., the main summary or frequency) emits its first value.
    - **Dependency Injection:** Use Cases in `:domain` MUST NOT use `@Inject` if the module does not include the Hilt plugin; they must be explicitly provided via a Hilt `@Module` in the `:data` layer.
- **Mappers:** Pure functions to isolate database entities from domain logic.
    - **Status Enum Consistency:** Enforce uppercase raw values for status enums (e.g., `FAILED`) to match technical specifications and ensure cross-platform consistency.
    - **Resilient Mapping:** When mapping from storage (String) to Domain (Enum), mappers MUST use `.uppercase()` on the status string (e.g., `ScribbleStatus.valueOf(status.uppercase())`) to handle case-insensitive database values safely.
    - **Formatting Use Cases:** Complex formatting logic that requires business rules (e.g., grouping sets as "3x10, 1x8 @ 80kg") MUST be implemented as a Domain Use Case (e.g., `FormatExerciseSummaryUseCase`). This ensures identical formatting logic across Android and iOS and prevents "formatting leak" into the UI layer.
- **Pure State Enforcement:** `State` classes MUST remain pure data containers. 
    - **No Logic Orchestration:** Use Cases MUST NOT be instantiated or orchestrated within the `State` class. 
    - **Pre-Mapped UI Models:** ViewModels MUST inject the necessary formatting Use Cases and pass pre-mapped, ready-to-display UI models to the `State`. 
    - **Resource Resolution Exception:** On Android, `State` classes MAY contain `@get:Composable @get:ReadOnlyComposable` getters for resolving `strings.xml` resources, but these getters MUST NOT contain business logic or formatting orchestration.
- **End-to-End Nullability:** If a domain property is nullable (e.g., `Double?`), the entire architectural chain (Intent -> Use Case -> Repository -> DAO) MUST explicitly support nullability to allow "clearing" values. Use Cases MUST NOT force non-null defaults unless required by business logic.
- **Data Export:** Entities and Domain Models intended for JSON export MUST be annotated with `@Serializable` from `kotlinx.serialization`.

## 3. UI & Design System (DRY)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Genericism:** Components in `:core:designsystem` MUST be strictly agnostic of business logic, feature-specific models, or navigation entities. They must use generic UI-specific data models (e.g., `Painter`, `String`, `Boolean`) to ensure maximum reusability across features.
- **Iconography:** Prefer `androidx.compose.material:material-icons-extended` for advanced iconography (e.g., Visibility, brand-specific symbols).
- **Atomic Composable Pattern (One File, One Composable):**
    - **Isolation:** Every significant or reusable Composable (e.g., `CanvasBody`, `ScribbleCard`) MUST reside in its own dedicated Kotlin file. Avoid nesting multiple top-level Composables in a single file.
    - **Organization:** Store these components in a `components/` sub-package within the feature's `ui` package.
    - **Main Screen Minimalism:** Feature main screens (e.g., `CanvasScreen.kt`) MUST only contain high-level layout, `Scaffold`, and state-to-intent wiring, delegating all section and item rendering to individual component files.
    - **Early Splitting:** Large screens MUST be split into contextual components (e.g., `TopBar`, `Body`, `Footer`) as soon as the implementation logic becomes non-trivial, rather than waiting for a 300-line threshold. This facilitates parallel development and cleaner PRs.
    - **State Resolution:** Main screens MUST resolve the `State` and map user interactions to `Intent` callbacks, passing only the necessary data or functional references to child components to keep them decoupled from the ViewModel.
- **Navigation UI Mapping:** The responsibility for mapping navigation destinations (e.g., `Screen`) to their UI representation (icons, localized labels) resides in the feature UI layer consuming the navigation component. This ensures feature modules remain independent of each other's UI details.
- **Nullable Numeric Inputs:**
    - **State Initialization:** Editable numeric fields in `State` MUST handle null domain values by providing a sensible UI default (e.g., `set.weight?.toString() ?: "0"`) via `@get:Composable`.
    - **Input Handling:** UI callbacks for numeric inputs MUST support empty strings. If the underlying data is nullable, an empty string input MUST be propagated as `null` through the Intent to the Use Case.
- **IME & Adaptive Layouts:**
    - **Keyboard Handling:** Screens with persistent text inputs MUST use `android:windowSoftInputMode="adjustResize"` in `AndroidManifest.xml`. Use `.imePadding()` on footers and `.imeNestedScroll()` on scrollable containers.
    - **Adaptive Sizing:** For large screens (>600dp), UI elements like input bars MUST use `.widthIn(max = 300.dp)` to maintain aesthetic balance.
    - **BottomSheet Scrollability:** Bottom sheets containing dynamic lists (e.g., model selection) MUST use `LazyColumn` to prevent overflow on smaller screens.
    - **Dynamic Visibility:** Conditionally expanding or collapsing UI sections MUST use `AnimatedVisibility` for smooth, editorial-style transitions.
- **Resources:**
    - **Zero Hardcoding:** All strings MUST come from `strings.xml`.
    - **Local Choice:** Navigation labels and feature-specific strings MUST reside in the feature module's `strings.xml` to maintain encapsulation.
    - **Strict State Resolution:** All UI strings (labels, hints, placeholders, content descriptions, and formatted messages) MUST be resolved in the `State` class via `@get:Composable @get:ReadOnlyComposable` properties.
    - **Component Isolation:** Contextual components (e.g., `Body`, `Footer`) MUST NOT call `stringResource()` directly. They must receive pre-resolved `String` parameters from the parent screen or state.
    - **Formatting:** String formatting (e.g., using `args`) MUST happen within the `State` getters to keep components pure and focused on layout.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent targets (`:domain`, `:data`, `:ui`).
- **Core Modules:** Shared functionality (`:core:database`, `:core:network`).
- **Core Isolation:** Changes to `core:` modules (e.g., Room entities, DAOs, common utilities) MUST be implemented, validated, and committed separately before being used in feature-layer implementations.
- **Error Handling:** Use Cases MUST return domain-specific error objects (e.g., `ScribbleError.NotFound`) via sealed interfaces/classes instead of throwing custom exceptions. This ensures predictable error propagation through the MVI state.
- **Debounced Persistence:** For Canvas-style editing or live-updating drafts, the ViewModel MUST implement debounced persistence. Use a `Job` with a short delay (e.g., 500ms) that is cancelled and restarted on every user input to prevent excessive database I/O while ensuring data is saved automatically.
- **AI Integration:** For AI-driven features (parsing, summaries), use the `com.google.firebase:firebase-ai` SDK and integrate via `:feature:ai:domain`'s `LLMEngine`. Use dedicated Use Cases for AI operations (e.g., `GetAIOverviewUseCase`).
    - **Dynamic Initialization:** LLM model instances MUST be initialized dynamically (e.g., via a getter or helper function) rather than stored as long-lived properties. This ensures they always pick up the latest reactive configuration (e.g., model name changes in `SystemConfig`) for each request.
    - **JSON Sanitization:** AI responses intended for parsing MUST be sanitized to remove potential Markdown code block markers (e.g., ` ```json ... ``` `) before decoding.
    - **Backend Targeting:** For the free tier/direct Gemini access, explicitly specify the `googleAI()` backend in the SDK initialization to distinguish it from Vertex AI on Google Cloud.
    - **Context Optimization:** AI operations intended for analysis (e.g., generating summaries or trends) MUST receive structured Domain Models (e.g., `List<Exercise>`) instead of raw text or unstructured entities (e.g., `Scribble`) to ensure high-quality, reproducible LLM output.
    - **Prompt Realism:** LLM prompt instructions MUST be logically consistent with the context provided. Do not instruct the AI to reference historical data or "previous" context if that data is not explicitly included in the inference payload.
    - **UX Consistency:** Asynchronous AI tasks MUST provide visual feedback via skeleton loading states (e.g., `AIInsightsLoadingSection`) to prevent UI "popping" and manage user expectations during inference latency.
    - **Token Optimization:** AI generation triggers MUST be optimized to avoid redundant calls. For date-based features, track the `lastGeneratedDate` in the ViewModel/Store and only trigger a refresh if the date has changed or if manually forced by the user.
- **Dependency Injection:** Hilt bindings in `:data` (DIP).
- **Coroutines:** `Dispatchers.Default` (Domain logic) and `Dispatchers.IO` (I/O).

- **API Key Management:** Sensitive keys (e.g., `GEMINI_API_KEY`) MUST NOT be committed to version control.
    - **Injection Pattern:** Use `local.properties` in the project root to store keys locally.
    - **Gradle Bridge:** Read keys in the relevant module's `build.gradle.kts` using `java.util.Properties` and expose them via `buildConfigField`.
    - **Runtime Validation:** Implementations using these keys MUST validate their presence at runtime and provide clear instructions in the error message for developers to fix their local environment.
    - **Security Trade-off:** API keys injected via `BuildConfig` are suitable for development but are inherently insecure for production. For production releases, prefer server-side proxying or robust client-side protection mechanisms like Firebase App Check.

## 5. Navigation
- **Custom Navigator:** Centralized logic in `:core:navigation`. No side effects.

## 6. Global Configuration Management
- **Source of Truth:** Global app settings (e.g., theme, units, AI provider) are managed by `ConfigRepository` and persisted in the `system_config` table.
- **Workflow for New Settings:** Adding a global configuration setting MUST follow this 8-step synchronization pattern:
    1.  **Domain:** Add the property to `SystemConfig` in `:core:config:domain`.
    2.  **Database Entity:** Add the property to the `SystemConfig` entity in `:core:database`.
    3.  **Data Mapper:** Update `toDomain()` and `toEntity()` in `:core:database:mapper` to include the new property.
    4.  **Repository Implementation:** Update the `defaultConfig` in `ConfigRepositoryImpl` (within `:core:config:data`) with a sensible initial value.
    5.  **App State:** Add the property to `AppState` in the `:app` module to enable global propagation.
    6.  **App ViewModel:** Update the `combine` logic in `AppViewModel` to map the new property from `ConfigRepository.config` to `AppState`.
    7.  **Main Activity:** Pass the new property from `appState` to the root theme or navigation components as needed.
    8.  **Feature Settings:** Update `SettingsState`, `SettingsIntent`, and `SettingsViewModel` in `:feature:settings:ui` to allow user modification, ensuring the `updateConfig` utility is used for persistence.

## 7. Entity Lifecycle & Deletion Logic
- **Canonical vs. Instance Records:** Distinguish between canonical metadata (e.g., `Exercise`) and instance performance records (e.g., `WorkoutExercise`).
- **Unique Constraints:** Canonical entities MUST use `unique` indices on identifying columns (e.g., `name`).
- **Data Parity (workoutId):** Cross-platform data parity MUST be maintained by linking `Scribble` and `Workout` entities via a `workoutId` field (Long). This ID acts as the definitive linkage for identifying which workout was generated from a specific scribble, enabling seamless transitions between the Canvas and Ledger views.
- **Conflict Resolution:** For `unique` column insertions, use `OnConflictStrategy.IGNORE`. Repository methods MUST manually check for the existing record's ID if insertion returns `-1L` to prevent duplicates.
- **Mapping Integrity:** Mappers converting instance hierarchies (e.g., `WorkoutExerciseWithDetails`) to domain models MUST map the instance's unique primary key (e.g., `workoutExerciseId`) to the domain model's `id`. This ensures subsequent deletion or update operations target the specific instance, not the canonical record.
- **Intelligent Synchronization Pattern:** When updating a parent record that owns shared child instances (e.g., a Scribble or Workout owning Exercises), the Repository MUST perform in-place synchronization instead of a blind "clear and re-insert".
    1. **Iterative Update:** Match existing items by ID. Update their properties in place instead of replacing the entire record.
    2. **Orphan-Aware Deletion:** When a child is removed from a parent relationship, only delete the child record if it has no other remaining parent relationships (e.g., a `WorkoutExercise` removed from a `Scribble` but still linked to a `Workout` MUST NOT be deleted).
    3. **Recursive Sync:** Apply the same logic down the object graph (e.g., from `Exercise` to its `Sets`).
    4. **Maintain Identity:** This prevents UI flickering and maintains object identity in reactive streams.
- **CASCADE Precision:** Deletion logic MUST target the instance record (e.g., `WorkoutExercise`). Rely on `ForeignKey.CASCADE` to ensure atomic cleanup of dependent child records (e.g., `WorkoutSet`). Use cases MUST avoid redundant manual checks or deletions for entities handled by CASCADE. Never delete canonical metadata as a side effect of removing an instance.

## 8. Daily History Grouping
- **Goal:** Aggregating multiple discrete logs (scribbles/sessions) into a single date-based view item for history-based features (e.g., Ledger).
- **State-Managed Aggregation:** Aggregation logic MUST NOT live in the Repository or Use Case. It MUST reside in the UI `State` data class as a derived property.
- **Flattened Hierarchy:** All child records (e.g., `Exercise`) from all sessions on a given date MUST be flattened into a single list within a `DailyWorkout` (or similar) UI model.
- **Sorting Consistency:**
    - **Days:** The main collection MUST be sorted by date descending (newest days first).
    - **Intra-day:** Within each day, sessions or exercises SHOULD be flattened in chronological order (ascending by time).
- **UI Representation:** Each day MUST be represented by a single UI container (e.g., a Card with `scribbleGlass`) containing a date header and the combined list of all activities for that day, regardless of how many individual sessions were recorded.

## 9. History & Time Integrity
- **Rolling History Pattern:** Features displaying historical data (e.g., Ledger, Insights) MUST default to a "Rolling 30-Day Window" (e.g., `LocalDate.now().minusDays(30)` to `LocalDate.now()`). This ensures the UI remains focused on recent, relevant activity while providing a consistent starting point across features.
- **Local Day Boundary Rule:** All date-to-timestamp conversions for database queries MUST use the system's default timezone (`ZoneId.systemDefault()`). 
    - **Query Start:** `startDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()`
    - **Query End:** `endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()`
    This rule is MANDATORY for all features dealing with aggregations (e.g., Insights, Frequency counts), ensuring that logs recorded at the edges of a day are correctly included or excluded based on the user's local context, preventing "missing history" bugs caused by UTC shifts.

## 10. Data Visualization (Canvas Charting)
- **Sequential Charting Pattern:** To ensure cross-platform parity and deterministic layering when using a primitive `Canvas`, all charts MUST be rendered in the following strict order:
    1.  **Infrastructure (Y-Axis):** Draw axis labels and grid lines first to establish the vertical scale.
    2.  **Atmosphere (Fill):** Draw area gradients next.
    3.  **Skeleton (Path):** Draw the trend line (e.g., using cubic Bézier curves) over the fill.
    4.  **Highlights (Points):** Draw data points using the "Halo Point Effect" to ensure they "sit" on top of the line.
    5.  **Context (X-Axis):** Draw time-based labels last.
- **Halo Point Effect:** To create visual separation between a data point and the trend line without using platform-specific shadows:
    1.  **The Halo:** Draw a larger circle (radius `R1`) filled with the chart's background color (e.g., `surfaceContainerLow`).
    2.  **The Point:** Draw a smaller circle (radius `R2 < R1`) in the primary data color centered on the same point.
    3.  This pattern effectively "cuts out" the line behind the point, ensuring maximum legibility.
