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
- **Use Cases:** Explicitly isolated to `@MainActor` to match the Store and Repository, or conform to `Sendable`.

### 3. Data Layer (The Implementation)
- **Reactive Repositories:** Use `Combine.PassthroughSubject` or `Observation` to signal data changes to open `AsyncStream` cursors. This is critical for real-time UI updates.
- **SwiftData Concurrency:** Handling `ModelContext` requires strict actor boundaries. Use `@preconcurrency import Combine` where necessary.
- **AI Integration:** For AI-driven features (parsing, summaries), integrate with equivalent LLM services via async/await. Define protocols like `LLMProvider` in the domain for decoupled integration.
- **No Mocks in Production:** Configure real `SwiftData` containers in `App.swift` immediately.

### 4. UI Layer (The View)
- **Design System:** Use `CoreDesignSystem` tokens.
- **Contextual Splitting:** Views MUST be split into contextual components (e.g., `HeaderView`, `BodyView`, `FooterView`).
- **Component Isolation:** Each major contextual area should be implemented as a separate SwiftUI `View` to ensure focus and testability.
- **Dynamic Theming:** Implement Light/Dark mode parity using a code-based `ThemeProvider` and `EnvironmentKey`.
- **Theme Concurrency:** Theme structs and `EnvironmentKey` default values MUST be `Sendable`.
- **Native OS Aesthetics (iOS 26+):** Use the native `.glassEffect(.regular.interactive())` modifier.
- **Platform Parity:** Maintain brand parity. Use native modifiers to enhance the OS-specific feel.

## Committing Workflow
1. `feat(domain): [feature] models and logic`
2. `feat(core): [feature] shared infra`
3. `feat(data): [feature] reactive implementation`
4. `feat(ai): [feature] AI integration and logic`
5. `feat(ui): [feature] MVI screens with dynamic theming and native glass effects`
