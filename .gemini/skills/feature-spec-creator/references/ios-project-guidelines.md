# ScribbleFit Core Guidelines (iOS)

## 1. Architectural Pattern: MVI (Model-View-Intent)
- **Store/ViewModel:** Autonomous `@Observable` classes (no base inheritance).
- **State:** Immutable `struct`. String resolution MUST happen here via computed properties.
- **Intent:** `enum` for user actions.
- **Business Logic:** Zero logic in Store; all logic resides in Use Cases (SRP).

## 2. Domain & Data Layers (SOLID)
- **Repository Protocol:** Defined in `Domain` (Dependency Inversion).
- **Implementation:** Defined in `Data` (Protocol based).
- **Use Cases:** The only place for business logic. Single responsibility.
- **Mappers:** Extensions or pure functions to isolate database models from domain logic.

## 3. UI & Design System (DRY)
- **Design System:** Use `CoreDesignSystem` tokens (colors, spacing, typography).
- **Composable Structure:** Small, single-responsibility Views in `Components/`.
- **Resources:** All strings must come from `Localizable.xcstrings`. No hardcoded text.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent SPM targets (`Domain`, `Data`, `UI`).
- **Core Modules:** Shared targets (`CoreDatabase`, `CoreNetwork`).
- **Dependency Injection:** Container-based or constructor injection (DIP).
- **Concurrency:** `Task` priorities (Domain logic) and `async/await` (I/O).

## 5. Navigation
- **Custom Navigator:** Centralized logic in `CoreNavigation`. No side effects.
