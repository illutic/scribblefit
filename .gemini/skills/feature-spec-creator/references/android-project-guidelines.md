# ScribbleFit Core Guidelines (Android)

## 1. Architectural Pattern: MVI (Model-View-Intent)
- **ViewModel:** Autonomous classes (no base inheritance).
- **State:** Immutable `data class`. String resolution MUST happen here via `@Composable @ReadOnlyComposable` getters.
- **Intent:** `sealed interface` for user actions.
- **Business Logic:** Zero logic in ViewModel; all logic resides in Use Cases (SRP).

## 2. Domain & Data Layers (SOLID)
- **Repository Interface:** Defined in `:domain` (Dependency Inversion).
- **Implementation:** Defined in `:data` (Interface based).
- **Use Cases:** The only place for business logic. Single responsibility.
- **Mappers:** Pure functions to isolate database entities from domain logic.

## 3. UI & Design System (DRY)
- **Design System:** Use `:core:designsystem` tokens (colors, spacing, typography).
- **Composable Structure:** Small, single-responsibility components in `components/`.
- **Resources:** All strings must come from `strings.xml`. No hardcoded text.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent targets (`:domain`, `:data`, `:ui`).
- **Core Modules:** Shared functionality (`:core:database`, `:core:network`).
- **Dependency Injection:** Hilt bindings in `:data` (DIP).
- **Coroutines:** `Dispatchers.Default` (Domain logic) and `Dispatchers.IO` (I/O).

## 5. Navigation
- **Custom Navigator:** Centralized logic in `:core:navigation`. No side effects.
