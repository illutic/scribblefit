# ScribbleFit Core Guidelines (iOS)

## 1. Architectural Pattern: Pure SwiftUI MVI (Model-View-Intent)
- **No UIKit:** Specifications MUST design UI solely for native SwiftUI.
- **Store/ViewModel:** Autonomous `@Observable` @MainActor classes.
- **Dynamic Theming:** All specifications MUST define light and dark mode mappings that maintain parity with the Android design system.
- **Modern Native Parity:** Balance Android parity with iOS-native aesthetics. For iOS 26+, utilize `.glassEffect(.regular.interactive())` for inputs and buttons.

## 2. Domain & Data Layers (Reactive & SOLID)
- **Swift 6 Concurrency:** Enforce `Sendable` domain models, `Sendable` theme structures, and `@MainActor` stores.
- **Reactive Contracts:** Any repository method returning a stream MUST be reactive, ensuring data changes trigger immediate UI updates.
- **Implementation:** Mandate `SwiftData` from the start.

## 3. UI & Design System (DRY)
- **Design System:** Pure SwiftUI `Color` extensions only.
- **Centralized Theming:** Use a `ThemeProvider` to inject dynamic brand colors into the environment.
- **Native Materials:** Use `.glassEffect()` for overlays to ensure a high-fidelity, OS-native feel.
- **Custom Shapes:** Use `UnevenRoundedRectangle`.

## 4. Modularity & Infrastructure
- **Feature Modules:** Independent SPM targets.
- **Target Versions:** Target iOS 26.0+ to support modern native design modifiers.
- **No Mocks in Production:** Specs must guide the implementation to configure real dependencies in the `App.swift` entry point immediately.

## 5. Navigation
- **Custom Navigator:** Centralized logic in `CoreNavigation`. No side effects.
