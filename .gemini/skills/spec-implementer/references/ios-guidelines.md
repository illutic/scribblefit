# iOS Implementation Guidelines (spec-implementer)

## Core Principles
- **SOLID:** Use Cases (SRP), Protocol-based Repositories (DIP/OCP).
- **DRY:** Reuse `CoreDesignSystem` components.
- **KISS:** Keep Views and Use Cases simple and focused.
- **Autonomous Modularity:** Targets MUST be independent; share logic only via `Core` targets.

## Architecture: MVI without Protocols/Base Classes

### 1. ViewModel (Store) Structure
Each Store must be autonomous using `@Observable`.
- **Zero Business Logic:** Stores only orchestrate UI state via Use Cases. No logic.
- **State:** A `struct` representing the UI state.
    - **No Hardcoded Strings:** Use computed properties to resolve strings from `Localizable.xcstrings`.
- **Intent:** An `enum` for user actions.
- **Navigation:** Inject a `Navigator`. No side effects.

### 2. Domain Layer (The Brain)
- **Models:** Simple `struct` types.
- **Repository Protocol:** Define the contract here (Dependency Inversion).
- **Use Cases:** The only place for business logic.
    - **SRP:** Each Use Case handles exactly one action.
    - **Concurrency:** Use `Task` with default priority.
    - **Error Handling:** Return `Result<T, Error>`. Call `Task.checkCancellation()`.
    - **Reuse:** Use Cases can depend on other Use Cases (DRY).

### 3. Data Layer (The Implementation)
- **Repository Implementation:** Implements the Domain protocol.
- **Concurrency:** Use `async/await` for I/O operations.
- **DI:** Registration logic resides here to bind implementations (DIP).
- **Mappers:** Extensions or pure functions to map between SwiftData models and Domain models.

### 4. UI Layer (The View)
- **Design System:** Use `CoreDesignSystem` tokens (colors, spacing, typography).
- **Composable Structure:** Small, single-responsibility Views in a `Components/` folder.
- **Composition over Inheritance:** Build screens by composing these small Views.

## Committing Workflow
1. `feat(domain): [feature] models and logic (verified with unit tests)`
2. `feat(core): [feature] shared infra (database/network)`
3. `feat(data): [feature] implementation and DI`
4. `feat(ui): [feature] MVI screens and components`
