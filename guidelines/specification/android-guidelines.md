# Android Implementation Guidelines (spec-implementer)

## Core Principles
- **SOLID:** Use Cases (SRP), Interface-based Repositories (DIP/OCP).
- **DRY:** Reuse `:core:designsystem` and common `:core:common` utilities.
- **KISS:** Keep Composables and Use Cases simple and focused.
- **Autonomous Modularity:** Features MUST be independent; share logic only via `:core`.

## Architecture: MVI without Base Classes

### 1. ViewModel Structure
Each ViewModel must be autonomous. Do not inherit from a `BaseViewModel`.
- **Zero Business Logic:** ViewModels orchestrate UI state by calling Use Cases. No validation or logic.
- **State:** A `data class` representing the entire UI state.
    - **No Hardcoded Strings:** Use `@get:Composable @get:ReadOnlyComposable` getters within `State` to resolve strings from `strings.xml`.
    - **Resolution Rules:** Label, hint, content description, and formatted string resolution MUST occur in the `State`.
    - **Contextual Components:** Components (like `Header`, `Body`, `Footer`) MUST NOT call `stringResource()` directly. They should receive pre-resolved `String` parameters.
    - **Formatting:** Formatting logic (e.g., `stringResource(R.string.format, arg)`) MUST be encapsulated in the `State` class.
- **Intent:** A `sealed interface` for user actions.
- **Navigation:** Inject `Navigator` directly. No side effects.

### 2. Domain Layer (The Brain)
- **Models:** Plain data classes.
- **Data Export:** Models intended for JSON export MUST be annotated with `@Serializable`.
- **Repository Interface:** Define the contract here (Dependency Inversion).
    - **Atomic Hierarchy:** Methods for UI lists MUST return full hierarchies in a single `@Transaction` (via the data layer implementation) to prevent UI flicker or partial data states.
- **Capability-Awareness:** Interfaces for external services (AI, etc.) MUST include methods to verify device support (e.g., `isSupported(): Boolean`) so the UI can gracefully degrade or adapt.
- **Use Cases:** The only place for business logic.
    - **SRP:** Each Use Case handles exactly one action.
    - **Coroutines:** Use `Dispatchers.Default`.
    - **Reactive Mastery:** Use `flatMapLatest` when reacting to parameter flows (e.g., date selection) to maintain strictly one active data subscription.
    - **Error Handling:** Return `Result<T>` using `runCatchingWithCancellation`.
    - **Reuse:** Use Cases can depend on other Use Cases (DRY).
    - **Dependency Injection:** Use Cases in `:domain` MUST NOT use `@Inject` if the module does not include the Hilt plugin; they must be explicitly provided via a Hilt `@Module` in the `:data` layer.

### 3. Data Layer (The Implementation)
- **Repository Implementation:** Implements the Domain interface.
- **Coroutines:** Use `Dispatchers.IO` for I/O operations.
- **DI:** Hilt `@Module` classes reside here to bind implementations (DIP).
- **Mappers:** Pure functions to map between Entities and Domain Models.
- **Entities:** Room entities intended for JSON export MUST be annotated with `@Serializable`.
- **Core Isolation:** Changes to `core:database` (entities, DAOs) MUST be implemented and committed separately from feature layers.
- **AI Integration:** For AI-driven features (parsing, summaries), integrate with `:feature:ai:domain`'s `LLMEngine`. Use dedicated Use Cases for AI operations (e.g., `GetAIOverviewUseCase`).

### 4. UI Layer (The View)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Iconography:** Use `material-icons-extended` for advanced iconography (Visibility, etc.).
- **Atomic Composable Pattern (One File, One Composable):**
    - **Isolation:** Every significant or reusable Composable (e.g., `CanvasBody`, `ScribbleCard`) MUST reside in its own dedicated Kotlin file. Avoid nesting multiple top-level Composables in a single file.
    - **Organization:** Store these components in a `components/` sub-package within the feature's `ui` package.
    - **Main Screen Minimalism:** Feature main screens (e.g., `CanvasScreen.kt`) MUST only contain high-level layout, `Scaffold`, and state-to-intent wiring, delegating all section and item rendering to individual component files.
    - **Early Splitting:** Large screens MUST be split into contextual components (e.g., `TopBar`, `Body`, `Footer`) as soon as the implementation logic becomes non-trivial, rather than waiting for a 300-line threshold.
    - **State Resolution:** Main screens MUST resolve the `State` and map user interactions to `Intent` callbacks, passing only the necessary data or functional references to child components to keep them decoupled from the ViewModel.
- **IME Handling:** Always use `android:windowSoftInputMode="adjustResize"`. Apply `.imePadding()` to bottom-anchored components and `.imeNestedScroll()` to scrollable content.
- **Adaptive Layouts:** On large screens (>600dp), constrain width-sensitive elements (like search bars or action buttons) using `.widthIn(max = 300.dp)`.
- **Composition over Inheritance:** Build screens by composing these small, single-responsibility parts.

## Committing Workflow
1. `feat(domain): [feature] models and logic (verified with unit tests)`
2. `feat(core): [feature] shared infra (database/network)`
3. `feat(data): [feature] implementation and DI`
4. `feat(ai): [feature] AI integration and logic`
5. `feat(ui): [feature] MVI screen and components`
