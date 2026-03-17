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
    - **No Hardcoded Strings:** Use `@Composable @ReadOnlyComposable` getters within `State` to resolve strings from `strings.xml`.
- **Intent:** A `sealed interface` for user actions.
- **Navigation:** Inject `Navigator` directly. No side effects.

### 2. Domain Layer (The Brain)
- **Models:** Plain data classes.
- **Repository Interface:** Define the contract here (Dependency Inversion).
- **Use Cases:** The only place for business logic.
    - **SRP:** Each Use Case handles exactly one action.
    - **Coroutines:** Use `Dispatchers.Default`.
    - **Error Handling:** Return `Result<T>` using `runCatchingWithCancellation`.
    - **Reuse:** Use Cases can depend on other Use Cases (DRY).

### 3. Data Layer (The Implementation)
- **Repository Implementation:** Implements the Domain interface.
- **Coroutines:** Use `Dispatchers.IO` for I/O operations.
- **DI:** Hilt `@Module` classes reside here to bind implementations (DIP).
- **Mappers:** Pure functions to map between Entities and Domain Models.

### 4. UI Layer (The View)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Composable Structure:** Small, single-responsibility components in a `components/` sub-package.
- **Composition over Inheritance:** Build screens by composing these small parts.

## Committing Workflow
1. `feat(domain): [feature] models and logic (verified with unit tests)`
2. `feat(core): [feature] shared infra (database/network)`
3. `feat(data): [feature] implementation and DI`
4. `feat(ui): [feature] MVI screen and components`
