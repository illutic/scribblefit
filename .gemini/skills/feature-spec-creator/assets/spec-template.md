# Feature Specification: [Feature Name]

## 1. Overview
[Brief, high-level explanation of the feature and its purpose.]

## 2. User Stories
- **As a [User Role]**, I want to [Action] **so that** [Benefit/Goal].
- [Example Story 1]

## 3. Acceptance Criteria
- [ ] Criterion 1
- [ ] Criterion 2
- [ ] **Contextual UI Splitting:**
    - [ ] **Header:** [Describe header content/behavior]
    - [ ] **Body:** [Describe body content/behavior]
    - [ ] **Footer:** [Describe footer content/behavior]

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Package Structure:** `:feature:[feature-name]` with `:data`, `:domain`, `:ui`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
- **AI (Optional):** Integration with `:feature:ai`'s `LLMEngine` for summaries/parsing.
- **Database:** Room with `Flow<T>` for reactivity.
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** SPM target `[FeatureName]Feature` with `Data`, `Domain`, `UI`.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
- **AI (Optional):** Integration with local/remote LLM via Swift Concurrency.
- **Database:** SwiftData with `AsyncSequence` for reactivity.
- **Background Tasks:** `BGTaskScheduler` and Swift Concurrency.

## 5. Validation
- **Unit Tests:** ViewModels/Stores, Use Cases, Mappers.
- **Integration Tests:** Database and Syncing logic.
- **UI Tests:** Compose Test Rule (Android) or XCUITest (iOS) for critical flows.
