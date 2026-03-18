# ScribbleFit Core Guidelines (Android)

## 1. Architectural Pattern: MVI (Model-View-Intent)
- **ViewModel:** Autonomous classes (no base inheritance).
- **State:** Immutable `data class`. String resolution MUST happen here via `@Composable @ReadOnlyComposable` getters.
- **Intent:** `sealed interface` for user actions.
- **Business Logic:** Zero logic in ViewModel; all logic resides in Use Cases (SRP).

## 2. Domain & Data Layers (SOLID)
- **Repository Interface:** Defined in `:domain` (Dependency Inversion).
- **Implementation:** Defined in `:data` (Interface based).
- **Use Cases:** The *only* place for business logic. Single responsibility. **Dependency Injection:** Use Cases in `:domain` MUST NOT use `@Inject` if the module does not include the Hilt plugin; they must be explicitly provided via a Hilt `@Module` in the `:data` layer.
- **Mappers:** Pure functions to isolate database entities from domain logic.

## 3. UI & Design System (DRY)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Composable Structure:**
    - **Contextual Splitting:** Screens MUST be split into contextual components (e.g., `Header`, `Body`, `Footer`).
    - **Component Isolation:** Each major contextual area should be implemented as a separate Composable function to ensure focus and testability.
    - **Single Responsibility:** Small, single-responsibility components in `components/`.
- **Resources:** All strings must come from `strings.xml`. No hardcoded text.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent targets (`:domain`, `:data`, `:ui`).
- **Core Modules:** Shared functionality (`:core:database`, `:core:network`).
- **AI Integration:** For AI-driven features (parsing, summaries), integrate with `:feature:ai:domain`'s `LLMEngine`. Use dedicated Use Cases for AI operations (e.g., `GetAIOverviewUseCase`).
- **Dependency Injection:** Hilt bindings in `:data` (DIP).
- **Coroutines:** `Dispatchers.Default` (Domain logic) and `Dispatchers.IO` (I/O).

## 5. Navigation
- **Custom Navigator:** Centralized logic in `:core:navigation`. No side effects.
