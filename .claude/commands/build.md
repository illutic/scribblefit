# Spec Implementer

Implement the feature specification described in $ARGUMENTS into the ScribbleFit codebase, layer-by-layer.

## Core Mandates
1. **Layer-by-Layer:** Implement layers in order: Domain -> Data -> UI.
2. **Commit Separately:** Commit each layer after implementation and verification.
3. **Core Isolation:** Any changes to `core:` modules (database, network) must be implemented and committed separately from feature layers.
4. **Single Platform Focus:** Focus on one platform (Android or iOS) at a time to maintain context.
5. **No Base Classes:** Every ViewModel/Store and Repository must be autonomous. Do not use or look for "Base" classes.
6. **No Hardcoded Strings:** Strictly forbid hardcoded strings in UI components. All UI text MUST be resolved via `strings.xml` (Android) or `Localizable.xcstrings` (iOS), preferably via the `State` class.
7. **Project Guidelines:** Strictly adhere to `guidelines/specification/android-guidelines.md` or `guidelines/specification/ios-guidelines.md`.
8. **Contextual UI Splitting:** Major screens MUST be split into contextual components (e.g., `Header`, `Body`, `Footer`) implemented as separate functions/views within the same layer.
9. **Explicit DI (Android):** Use Cases in the `:domain` layer should NOT use `@Inject`. Instead, they must be explicitly provided in a Hilt `@Module` within the `:data` layer.
10. **Dynamic Theme (iOS):** Theme providers MUST store content as an `@escaping` closure to ensure dynamic re-evaluation during environment changes (e.g., Dark Mode).

## Workflow

### 1. Research & Preparation
- Read the target spec in `specs/` that matches $ARGUMENTS.
- Identify the target platform (Android or iOS).
- Check the current state of the codebase to see if any parts are already implemented.

### 2. Domain Implementation
- **Goal:** Implement the "brain" of the feature.
- **Tasks:**
  - Define Domain Models.
  - Define Repository Interfaces (Android) or Protocols (iOS).
  - Implement Use Cases.
- **Validation:** Write unit tests for use cases and domain logic.
- **Commit:** `feat(domain): [feature] models and logic`

### 3. Core Infrastructure (Optional)
- **Goal:** Prepare the shared database or network layers.
- **Tasks:**
  - Add Room Entities/DAOs (Android) or SwiftData models (iOS).
  - Update `core:database` or `core:network`.
- **Validation:** Ensure the project builds.
- **Commit:** `feat(core): [feature] database schema updates`

### 4. Data Implementation
- **Goal:** Connect the domain to the source of truth.
- **Tasks:**
  - Implement the Repository.
  - Implement Data Sources (Local/Remote).
  - Implement Mappers between Entity and Domain Model.
- **Validation:** Integration tests for repositories if possible.
- **Commit:** `feat(data): [feature] repository implementation`

### 5. AI Integration (Optional)
- **Goal:** Implement AI-driven logic (parsing, summaries).
- **Tasks:**
  - Integrate with `:feature:ai` (Android) or equivalent LLM integration (iOS).
  - Implement `LLMEngine`-based Use Cases.
- **Validation:** Unit tests for AI parsing/summary logic.
- **Commit:** `feat(ai): [feature] AI integration and logic`

### 6. UI Implementation
- **Goal:** Implement the user interface using MVI.
- **Tasks:**
  - Define State and Intent (Android).
  - Implement the ViewModel (Android) or Store (iOS).
  - Use `Navigator` for navigation; handle transient UI (errors, messages) directly in the state.
  - Implement the `FeatureRoute` and `FeatureScreen`.
  - **Zero Hardcoding Audit:** Verify that no hardcoded strings exist in any Composable/View. Every visible string must be a resource lookup.
- **Validation:** Manual verification or UI tests.
- **Commit:** `feat(ui): [feature] MVI and screens`

## Reference Material
- **Android:** `guidelines/specification/android-guidelines.md`
- **iOS:** `guidelines/specification/ios-guidelines.md`
