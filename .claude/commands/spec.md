# Feature Spec Creator

Generate a comprehensive technical specification document for the ScribbleFit feature described in $ARGUMENTS.

## Workflow

1. **Analyze the Feature:** Understand the feature's purpose and its impact on the Android and iOS apps.
2. **Check Designs:** Search for and review relevant UI designs using the **Stitch MCP** tools (`list_projects`, `list_screens`, `get_screen`). Incorporate visual requirements and component hierarchies from these designs into the specification.
3. **Load Project Context:** Reference `guidelines/project/android-project-guidelines.md` and `guidelines/project/ios-project-guidelines.md` to ensure the "Development Guidelines" section aligns with ScribbleFit's codebase-verified architecture (MVI, Compose, SwiftData, etc.).
4. **Generate Spec:** Use the template structure below to produce the document.
5. **Save Output:** Write the spec to `specs/[feature-name].md`.

## Spec Template

```markdown
# Feature Specification: [Feature Name]

## 1. Overview
[Brief, high-level explanation of the feature and its purpose.]

## 2. User Stories
- **As a [User Role]**, I want to [Action] **so that** [Benefit/Goal].

## 3. Acceptance Criteria
- [ ] Criterion 1
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
```

## Principles
- **Overview:** Provide a clear, technical description.
- **User Stories:** Use the "As a... I want to... so that..." format.
- **Acceptance Criteria:** Include specific, measurable goals.
- **Contextual UI Splitting:** Mandate that the UI must be designed and specified as separate `Header`, `Body`, and `Footer` components.
- **AI-First Features:** If the feature involves AI (summaries, parsing, insights), specify integration with `:feature:ai`'s `LLMEngine` (Android) or equivalent LLM integration (iOS). Mandate specific loading and empty states for AI-generated content.
- **Design Alignment:** Ensure the "Acceptance Criteria" and "UI" sections reflect the specific components and layouts found in the Stitch designs.
- **Validation:** Detail a testing strategy covering unit, integration, and UI tests.
