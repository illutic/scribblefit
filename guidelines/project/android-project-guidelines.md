# ScribbleFit Core Guidelines (Android)

## 1. Architectural Pattern: MVI (Model-View-Intent)
- **ViewModel:** Autonomous classes (no base inheritance).
- **State:** Immutable `data class`. String resolution MUST happen here via `@Composable @ReadOnlyComposable` getters.
- **Intent:** `sealed interface` for user actions.
- **Business Logic:** Zero logic in ViewModel; all logic resides in Use Cases (SRP).

## 2. Domain & Data Layers (SOLID)
- **Repository Interface:** Defined in `:domain` (Dependency Inversion).
- **Atomic Fetching:** Repository methods intended for UI lists MUST return the full nested hierarchy (e.g., using Room's `@Relation`) in a single `@Transaction` to ensure UI consistency. If a single-query hierarchy is not feasible due to complexity or dynamic relationships, the Use Case MUST orchestrate enrichment using `flatMapLatest` and `combine` to ensure an atomic emission of the final list.
- **Implementation-Specific Use Cases:** Capability checks or configuration-specific logic (e.g., `isLocalSupported()`) MUST NOT be part of a shared domain interface (e.g., `LLMEngine`). Instead, create a dedicated Use Case (e.g., `CheckLocalSupportUseCase`) that depends directly on the concrete implementation (e.g., `LocalAIEngine`) to keep the shared interface clean and focused on common functionality.
- **Implementation:** Defined in `:data` (Interface based).
- **Use Cases:**
    - **Single Responsibility:** The *only* place for business logic.
    - **Reactive Mastery:** Use Cases that depend on a `Flow` of parameters (e.g., a date flow from the UI) MUST use `flatMapLatest` to ensure only the most recent parameter's stream is active.
    - **Reactive Enrichment:** When deep child relations (e.g., Exercise -> Sets) are not included in the base list query, Use Cases MUST orchestrate enrichment using `flatMapLatest` and `combine` to ensure a single, consistent state emission.
    - **Dependency Injection:** Use Cases in `:domain` MUST NOT use `@Inject` if the module does not include the Hilt plugin; they must be explicitly provided via a Hilt `@Module` in the `:data` layer.
- **Mappers:** Pure functions to isolate database entities from domain logic.
    - **Status Enum Consistency:** Enforce uppercase raw values for status enums (e.g., `FAILED`) to match technical specifications and ensure cross-platform consistency.
    - **Resilient Mapping:** When mapping from storage (String) to Domain (Enum), mappers MUST use `.uppercase()` on the status string (e.g., `ScribbleStatus.valueOf(status.uppercase())`) to handle case-insensitive database values safely.
- **Data Export:** Entities and Domain Models intended for JSON export MUST be annotated with `@Serializable` from `kotlinx.serialization`.

## 3. UI & Design System (DRY)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Iconography:** Prefer `androidx.compose.material:material-icons-extended` for advanced iconography (e.g., Visibility, brand-specific symbols).
- **Composable Structure:**
    - **Contextual Splitting:** Screens MUST be split into contextual components (e.g., `Header`, `Body`, `Footer`).
    - **File-Level Isolation:** When a screen exceeds 300 lines, contextual components MUST be moved to separate files within the same package (e.g., `CanvasTopBar.kt`, `CanvasFooter.kt`) to improve reviewability.
    - **Single Responsibility:** Small, single-responsibility components in `components/`.
- **IME & Adaptive Layouts:**
    - **Keyboard Handling:** Screens with persistent text inputs MUST use `android:windowSoftInputMode="adjustResize"` in `AndroidManifest.xml`. Use `.imePadding()` on footers and `.imeNestedScroll()` on scrollable containers.
    - **Adaptive Sizing:** For large screens (>600dp), UI elements like input bars MUST use `.widthIn(max = 300.dp)` to maintain aesthetic balance.
    - **BottomSheet Scrollability:** Bottom sheets containing dynamic lists (e.g., model selection) MUST use `LazyColumn` to prevent overflow on smaller screens.
    - **Dynamic Visibility:** Conditionally expanding or collapsing UI sections MUST use `AnimatedVisibility` for smooth, editorial-style transitions.
- **Resources:**
    - **Zero Hardcoding:** All strings MUST come from `strings.xml`.
    - **Strict State Resolution:** All UI strings (labels, hints, placeholders, content descriptions, and formatted messages) MUST be resolved in the `State` class via `@get:Composable @get:ReadOnlyComposable` properties.
    - **Component Isolation:** Contextual components (e.g., `Body`, `Footer`) MUST NOT call `stringResource()` directly. They must receive pre-resolved `String` parameters from the parent screen or state.
    - **Formatting:** String formatting (e.g., using `args`) MUST happen within the `State` getters to keep components pure and focused on layout.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent targets (`:domain`, `:data`, `:ui`).
- **Core Modules:** Shared functionality (`:core:database`, `:core:network`).
- **Core Isolation:** Changes to `core:` modules (e.g., Room entities, DAOs, common utilities) MUST be implemented, validated, and committed separately before being used in feature-layer implementations.
- **AI Integration:** For AI-driven features (parsing, summaries), integrate with `:feature:ai:domain`'s `LLMEngine`. Use dedicated Use Cases for AI operations (e.g., `GetAIOverviewUseCase`).
    - **Context Optimization:** AI operations intended for analysis (e.g., generating summaries or trends) MUST receive structured Domain Models (e.g., `List<Exercise>`) instead of raw text or unstructured entities (e.g., `Scribble`) to ensure high-quality, reproducible LLM output.
    - **UX Consistency:** Asynchronous AI tasks MUST provide visual feedback via skeleton loading states (e.g., `AIInsightsLoadingSection`) to prevent UI "popping" and manage user expectations during inference latency.
    - **Token Optimization:** AI generation triggers MUST be optimized to avoid redundant calls. For date-based features, track the `lastGeneratedDate` in the ViewModel/Store and only trigger a refresh if the date has changed or if manually forced by the user.
- **Dependency Injection:** Hilt bindings in `:data` (DIP).
- **Coroutines:** `Dispatchers.Default` (Domain logic) and `Dispatchers.IO` (I/O).

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
- **Conflict Resolution:** For `unique` column insertions, use `OnConflictStrategy.IGNORE`. Repository methods MUST manually check for the existing record's ID if insertion returns `-1L` to prevent duplicates.
- **Mapping Integrity:** Mappers converting instance hierarchies (e.g., `WorkoutExerciseWithDetails`) to domain models MUST map the instance's unique primary key (e.g., `workoutExerciseId`) to the domain model's `id`. This ensures subsequent deletion or update operations target the specific instance, not the canonical record.
- **Atomic Cleanup:** When updating a parent record that owns a list of child instances (e.g., a Scribble with exercises), the Use Case or Repository MUST perform an atomic "clear" of existing children before inserting new ones to prevent data stale or duplication on retries.
- **CASCADE Precision:** Deletion logic MUST target the instance record (e.g., `WorkoutExercise`). Rely on `ForeignKey.CASCADE` to clean up dependent child records (e.g., `WorkoutSet`). Never delete canonical metadata as a side effect of removing an instance.
