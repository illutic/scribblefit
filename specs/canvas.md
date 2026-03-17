# Feature Specification: Canvas

## 1. Overview
The Canvas is the home screen of ScribbleFit, designed for rapid, low-friction workout entry. It allows users to "scribble" their workout in plain text (e.g., "Bench press 100kg 3x10"). These scribbles are then processed by an LLM to extract structured workout data.

## 2. User Stories
- **As a fitness enthusiast**, I want to quickly type my workout in plain text **so that** I don't spend time navigating complex menus during my session.
- **As a user**, I want to see the parsing status of my scribbles **so that** I know when they have been successfully processed.
- **As a user**, I want to navigate to previous days **so that** I can review my past workout logs.

## 3. Acceptance Criteria
- [ ] Users can submit raw text scribbles for the current or a selected date.
- [ ] Scribbles transition through statuses: `PENDING`, `PARSING`, `SUCCESS`, `FAILED`, and `COMPLETED`.
- [ ] Successfully parsed scribbles display structured data (exercises, sets, reps, weight).
- [ ] Users can click on a successful scribble to open a management dialog (Confirm, Edit, Delete).
- [ ] Users can navigate between dates using "Previous" and "Next" controls.

## 4. Design & UI Components
The UI must strictly adhere to the "The Input Canvas (Home)" design project.

### Core Components:
- **`CanvasTopBar`**:
    - **Title**: "ScribbleFit" (Left-aligned).
    - **Action**: Settings/Profile button (Right-aligned icon).
- **`DateHeader`**:
    - **Display**: Current date (e.g., "Monday, March 16").
    - **Interaction**: Left/Right arrows to navigate between days.
- **`ScribbleList`**:
    - **Empty State**: Displays "Start scribbling. Type your first set below." (Mid Gray `#8E8EA0`).
    - **Populated State**: List of `ScribbleCard` components.
- **`ScribbleInputPill`**:
    - **Visual**: 52pt height, fully rounded capsule (Background: `#F7F7F8`).
    - **Placeholder**: "What did you lift today?" (Mid Gray `#8E8EA0`).
    - **Action Button**: Circular 32pt "Send" button (Up-arrow icon).
    - **Behavior**: 
        - Send button at 0.5 opacity when empty, 1.0 when typing.
        - Triggers `onSubmit()` and clears immediately on tap.
        - Optional light impact haptic feedback on submission.

### Visual Styling:
- **Background**: Very Soft Gray (`#F7F7F8`).
- **Accent**: ScribbleFit Blue (`#2b8cee`).
- **Typography**: 15pt Regular for input and labels.
- **Spacing**: 16pt horizontal margins for the input and cards.

## 5. Development Guidelines (Android)
- **Architecture**: MVI (Autonomous `CanvasViewModel`, `CanvasState`, `CanvasIntent`).
    - **State-Driven UI**: All strings and placeholders MUST be resolved in `CanvasState` via `@Composable @ReadOnlyComposable` getters (e.g., `textfieldPlaceholder`, `emptyScribbleText`). No hardcoded text in Composables.
- **Modularity**: Autonomous `:feature:canvas` module. Communicate with other features via `:core` modules only.
- **SOLID Domain Layer**:
    - **SRP Use Cases**: `AddRawScribbleUseCase`, `GetScribblesByDateUseCase`, `ParsePendingScribblesUseCase`.
    - **Error Handling**: Use `runCatchingWithCancellation` and return `Result<T>`.
    - **Coroutines**: `Dispatchers.Default` for Use Cases, `Dispatchers.IO` for Repositories.
- **Data Layer**: Hilt `@Module` bindings for Repositories and DataSources. Mappers for Entity -> Domain isolation.
- **UI**: Composable structure with small, single-responsibility components in `components/`. Strictly use `:core:designsystem` tokens.

## 6. Development Guidelines (iOS)
- **Architecture**: MVI (Autonomous `@Observable CanvasStore`, `CanvasState` struct, `CanvasIntent` enum).
    - **State-Driven UI**: All strings MUST be resolved in `CanvasState` via computed properties and `String(localized: "...")`. No hardcoded text in Views.
- **Modularity**: Independent `CanvasFeature` SPM target.
- **SOLID Domain Layer**:
    - **SRP Use Cases**: `AddRawScribbleUseCase`, `GetScribblesByDateUseCase`, `ParsePendingScribblesUseCase`.
    - **Error Handling**: Return `Result<T, Error>`. Call `Task.checkCancellation()`.
    - **Concurrency**: `Task` priorities for Domain, `async/await` for I/O.
- **Data Layer**: DI registration for Repository protocols. Mappers for SwiftData isolation.
- **UI**: Composable structure with small Views in `Components/`. Strictly use `CoreDesignSystem` tokens.

## 7. Validation
- **Unit Tests**:
    - `CanvasViewModel` / `CanvasStore` (State transitions).
    - Use Cases (Business logic and cancellation handling).
- **Integration Tests**: Repository and Database CRUD.
- **UI Tests**: Compose/XCUITest for "Add Scribble" flow and date navigation.
