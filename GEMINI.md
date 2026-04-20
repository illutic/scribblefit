# GEMINI.md

## Project Overview

ScribbleFit is a cross-platform (Android + iOS) fitness tracking app. Users write freeform text
scribbles that are parsed by AI into structured workout data. The app follows MVI architecture on
both platforms with strict layer separation (Domain -> Data -> UI).

## Epistemological Rigour

IMPORTANT: When creating a PR, never claim something is fixed without evidence. Always state your
certainty level:

| Level            | Meaning         | Evidence                     |
|------------------|-----------------|------------------------------|
| **INSUFFICIENT** | Looks right     | Visual inspection only       |
| **WEAK**         | Compiles        | Build succeeds               |
| **MODERATE**     | Manual test     | Ran the app / checked output |
| **STRONG**       | Automated tests | Unit/screen tests pass       |

Always provide the certainty level and the proof when reporting on changes.

## Rules

**Boy Scout:** Fix completely with tests, or don't touch it. No TODOs without Jira tickets. No
partial refactors. No features beyond scope.

**Tech Debt:** Any code added that does not strictly adhere to these guidelines (due to time 
constraints or technical blockers) MUST be documented in `TECH_DEBT.md`.

**Certainty Levels** — never claim "fixed" without evidence:

| Level        | Meaning         | Evidence                     |
|--------------|-----------------|------------------------------|
| INSUFFICIENT | Looks right     | Visual inspection only       |
| WEAK         | Compiles        | Build succeeds               |
| MODERATE     | Manual test     | Ran the app / checked output |
| STRONG       | Automated tests | Unit/screen tests pass       |

Always state level + proof when reporting changes.

**Prove It With Tests:** If you are not certain that an implementation works correctly, write a
test for it before reporting it as done. Use unit tests for domain/data logic, Compose UI tests
(Android) or XCUITests (iOS) for UI behaviour, or both. A certainty level of WEAK or INSUFFICIENT
is not acceptable for merged code — escalate to STRONG by adding automated tests.

**Compilation Rule:** A task is NOT complete until the application compiles successfully on ALL target platforms. For cross-platform changes, verify BOTH Android and iOS builds.

**QA Verification:** ALWAYS run the `qa-agent` after proving your changes with tests and verifying compilation. This agent 
verifies that the implementation meets all acceptance criteria in the feature spec and updates the 
`specs/[feature].md` file accordingly.

**Critical Review:** ALWAYS consult the `critical-reviewer` subagent before finalizing any code 
change. This agent assumes you are wrong and the codebase is wrong, helping identify hidden flaws 
and architectural violations.

**Post-Implementation Retrospection:** Run the `retrospection-agent` after every successful code 
change (and after the `critical-reviewer` has passed). This agent formalizes learnings, refines 
guidelines, and identifies repeatable patterns to improve future implementation efficiency.

**Session Learning:** Suggest `/retrospection` at the end of substantive sessions.

## Guidelines

Before implementing any feature or making architectural decisions, read the relevant guidelines:

- **Design System:** `guidelines/DESIGN.md` -- Editorial Minimalism ("Digital Atelier"),
  monochromatic palette, zero borders, glassmorphism
- **Android Project Guidelines:** `guidelines/project/android-project-guidelines.md` -- MVI, Hilt,
  Room, Compose, coroutines
- **iOS Project Guidelines:** `guidelines/project/ios-project-guidelines.md` -- Pure SwiftUI MVI,
  SwiftData, Swift 6 concurrency
- **Android Implementation Workflow:** `guidelines/specification/android-guidelines.md` --
  Committing strategy, UI splitting, adaptive layouts
- **iOS Implementation Workflow:** `guidelines/specification/ios-guidelines.md` -- Committing
  strategy, native aesthetics, Swift Charts

## Architecture Rules

- **MVI without base classes** -- Every ViewModel/Store, Repository, and UseCase is autonomous
- **Zero business logic in ViewModels/Stores** -- All logic lives in Use Cases (SRP)
- **Layer order:** Domain (models, use cases, interfaces) -> Data (implementations, mappers,
  persistence) -> UI (state, intent, screens)
- **Commit each layer separately** following the convention: `feat(domain):`, `feat(core):`,
  `feat(data):`, `feat(ai):`, `feat(ui):`
- **Core isolation:** Changes to `core:` modules must be committed before feature layers

## Android Specifics

- **UI:** 100% Jetpack Compose with `:core:designsystem` tokens
- **Strings:** Zero hardcoded strings. Resolve all UI text in `State` via
  `@get:Composable @get:ReadOnlyComposable` getters from `strings.xml`
- **DI:** Hilt. Use Cases in `:domain` must NOT use `@Inject`; provide them via `@Module` in `:data`
- **Persistence:** Room with `@Transaction` for atomic fetching
- **Coroutines:** `Dispatchers.Default` for domain, `Dispatchers.IO` for I/O
- **Screen splitting:** Split at 300 lines into contextual components (`Header`, `Body`, `Footer`)
- **IME:** Use `adjustResize` + `.imePadding()` + `.imeNestedScroll()`
- **Adaptive:** Constrain width-sensitive elements with `.widthIn(max = 300.dp)` on screens >600dp
- **Status enums:** Uppercase raw values, use `.uppercase()` in mappers
- **AI:** Integrate via `:feature:ai:domain`'s `LLMEngine`

## iOS Specifics

- **UI:** 100% SwiftUI (no UIKit). Target iOS 17+, use iOS 26 enhancements via `#available`
- **Strings:** Resolve all UI text in State/Store. Contextual components receive pre-resolved
  strings
- **Concurrency:** Swift 6 strict concurrency -- `Sendable` models, `@MainActor` stores/repos/use
  cases
- **Persistence:** SwiftData with reactive `AsyncStream` repositories
- **Mapping:** `@MainActor` on `toDomain()` extensions, bidirectional mapping required
- **AI:** `RoutingLLMService` for dynamic cloud/local switching
- **Status enums:** Uppercase raw values (e.g., `case failed = "FAILED"`), use `.uppercased()` in
  mapping

## Feature Specs

Feature specifications live in `specs/`. Read the relevant spec before implementing:

- `specs/canvas.md` -- Home screen, scribble entry, AI parsing
- `specs/ledger.md` -- Workout history
- `specs/insights.md` -- AI analytics and charts
- `specs/settings.md` -- User preferences and configuration

## Configuration Management

Adding a global setting follows an 8-step synchronization pattern across Domain -> Database
Entity -> Mapper -> Repository -> AppState -> AppViewModel -> MainActivity -> Settings UI. See
`guidelines/project/android-project-guidelines.md` Section 6 for details.

## Entity Lifecycle

- Distinguish canonical metadata (e.g., `Exercise`) from instance records (e.g., `WorkoutExercise`)
- Use `OnConflictStrategy.IGNORE` with unique constraints; manually check ID on insertion failure
- Map instance IDs (not canonical IDs) to domain model `id` fields
- Atomic clear of children before re-insert on parent updates
- `ForeignKey.CASCADE` for dependent cleanup; never delete canonical records as a side effect

## Skills

- **feature-spec-creator** (`/spec [feature]`) -- Generate a technical feature specification
- **spec-implementer** (`/build [feature]`) -- Implement a feature spec layer-by-layer
- **retrospection** (`/retrospect [feature]`) -- Analyze implementation and update guidelines

## Agents

- `android-expert` -- Senior Android engineer for Kotlin/Compose/Room/Hilt work
- `ios-expert` -- Senior iOS engineer for Swift/SwiftUI/SwiftData work
- `critical-reviewer` -- Harsh senior architect who assumes everything is wrong and hunts for flaws
- `retrospection-agent` -- Architect focused on formalizing patterns and refining guidelines after implementation
- `qa-agent` -- Meticulous QA engineer who verifies implementations against acceptance criteria in specs
