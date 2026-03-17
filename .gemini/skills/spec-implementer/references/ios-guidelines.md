# iOS Implementation Guidelines (spec-implementer)

## Core Principles
- **SOLID:** Use Cases (SRP), Protocol-based Repositories (DIP/OCP).
- **DRY:** Reuse `CoreDesignSystem` components.
- **KISS:** Keep Views and Use Cases simple and focused.
- **Autonomous Modularity:** Targets MUST be independent; share logic only via `Core` targets.

## Architecture: Pure SwiftUI MVI (Strictly NO UIKit)

### 1. ViewModel (Store) Structure
Each Store must be autonomous using `@Observable` and must be isolated to `@MainActor`.
- **Zero Business Logic:** Stores only orchestrate UI state via Use Cases.
- **State:** A `struct` representing the UI state.
- **Reactive Observation:** Ensure `observe` tasks are cancelled and restarted when key state (like dates) change.
- **No UIKit:** Absolutely no `import UIKit`. Use `UnevenRoundedRectangle` for custom corners.

### 2. Domain Layer (The Brain)
- **Models:** Simple `struct` types. All domain models MUST conform to `Sendable`.
- **Repository Protocol:** Define the contract here. Stream-based getters MUST be reactive.
- **Use Cases:** Explicitly isolated (e.g., `@MainActor`) or `Sendable`.

### 3. Data Layer (The Implementation)
- **Reactive Repositories:** Use `Combine.PassthroughSubject` or `Observation` to signal data changes to open `AsyncStream` cursors. This is critical for real-time UI updates.
- **SwiftData Concurrency:** Handling `ModelContext` requires strict actor boundaries. Use `@preconcurrency import Combine` where necessary.
- **No Mocks in Production:** Configure real `SwiftData` containers in `App.swift` immediately.

### 4. UI Layer (The View)
- **Design System:** Use `CoreDesignSystem` tokens.
- **Dynamic Theming:** Implement Light/Dark mode parity using a code-based `ThemeProvider` and `EnvironmentKey` to ensure exact hex-code alignment with Android design tokens.
- **Theme Concurrency:** Theme structs and `EnvironmentKey` default values MUST be `Sendable`.
- **Native OS Aesthetics (iOS 26+):** Use the native `.glassEffect(.regular.interactive())` modifier for interactive elements (inputs, buttons).
- **Platform Parity:** Maintain brand parity (colors, spacing, behavior). Use native modifiers to enhance the OS-specific feel without deviating from the brand identity.

## Committing Workflow
1. `feat(domain): [feature] models and logic`
2. `feat(core): [feature] shared infra`
3. `feat(data): [feature] reactive implementation`
4. `feat(ui): [feature] MVI screens with dynamic theming and native glass effects`
