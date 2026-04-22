# Fixed Technical Debt (Archive)

Items previously tracked in `TECH_DEBT.md` that have been resolved.

---

## Architectural Deviations

### 1. Incomplete Atomic Update Pattern [FIXED]
*   **Fix:** Implemented `ConfirmScribbleUseCase` to sync UI edits back to DB before re-linking. Fixed DAO methods for targeted updates.

### 2. Business Logic in ViewModels [FIXED]
*   **Fix:** Moved set re-indexing logic to `ReorderSetsUseCase` in the domain layer.

### 3. Brittle Date Calculations [FIXED]
*   **Fix:** DAOs updated to use robust range queries instead of fixed millisecond constants.

### 22. CanvasViewModel.onIntent Complexity [FIXED]
*   **Fix:** Extracted complex intent handling into specialized private methods (`handleScribbleIntent`, `handleDateIntent`, etc.) in both Android and iOS.

### 25. Business Logic in State/Store [FIXED]
*   **Fix:** Moved all volume and exercise summary formatting logic into Domain Use Cases (`FormatWorkoutSummaryUseCase`, `FormatExerciseSummaryUseCase`) on both platforms.

### 27. GetWorkoutWithExercisesUseCase Missing Dispatcher [FIXED]
*   **Fix:** Injected `CoroutineDispatcher` and applied `flowOn(Dispatchers.Default)` for domain logic consistency.

### 33. Status-Based Routing in View Body (iOS) [FIXED]
*   **Fix:** Moved routing logic from `CanvasView` body into `CanvasStore` intent handling, updating a centralized `navigationState`.

### 37. UseCase Instantiation in State (Android) [FIXED]
*   **Fix:** Removed direct instantiation of `FormatExerciseSummaryUseCase` in `State.kt`. Injected use cases into ViewModels and passed pre-mapped UI models to the state.

---

## Performance & Efficiency

### 4. N+1 Query Problem on Canvas [FIXED]
*   **Fix:** `GetScribblesForDateUseCase` now uses the `@Relation` data from `ScribbleWithExercises` directly.

### 5. Over-Collection in Insights [FIXED]
*   **Fix:** `InsightsViewModel` refactored to use `stateIn` with `flatMapLatest`.

### 19. Ledger GroupedWorkouts Losing IDs (iOS) [FIXED]
*   **Fix:** Refactored `LedgerState` to group by `Workout` objects instead of flattened `Exercise` lists, ensuring stable ID tracking for navigation.

### 28. ViewModel Race Condition on Weight Unit (Android) [FIXED]
*   **Fix:** Refactored `loadWorkout` to use reactive `combine` flow, ensuring the weight unit is always current during state emission.

### 29. One-Shot Fetch in WorkoutExercisesStore (iOS) [FIXED]
*   **Fix:** Replaced one-shot `getWorkout` call with reactive `observeWorkout(id:)` using `AsyncStream`.

---

## Resilience & UX

### 6. Stuck "Parsing" Status Recovery [FIXED]
*   **Fix:** `ParsePendingScribblesUseCase` now retries `PARSING` scribbles on app start and includes a session-level tracker to prevent redundant parsing.

### 7. Generic Error Handling [FIXED]
*   **Fix:** Implemented typed error classes (e.g., `ScribbleError`) to provide semantic context for failures, moving away from generic `Result<Unit>` exceptions.

### 20. Sheet Dismissal Flash (iOS) [FIXED]
*   **Fix:** Moved detail sheets from global `ContentView` to local feature views, preventing root state re-evaluations on dismissal.

### 23. Sheet Ownership in Root (iOS) [FIXED]
*   **Fix:** Decoupled detail sheets from root `ContentView` and moved them into `CanvasView` and `LedgerView`.

### 26. Missing Loading/Error States in WorkoutExercises (Android) [FIXED]
*   **Fix:** Implemented `CircularProgressIndicator` and error text handling in `WorkoutExercisesScreen.kt`.

### 31. Unstable Identity in Identifiable Helpers (iOS) [FIXED]
*   **Fix:** Updated `IdentifiableString` and `IdentifiableUUID` to use stable `id` derived from values instead of generating new UUIDs.

---

## Guideline Violations (UI & Layout)

### 10. Missing Adaptive Width Constraints [FIXED]
*   **Fix:** Applied `widthIn(max = 600.dp)` to content columns in `Canvas`, `Insights`, `Ledger`, and `ScribbleConfirmationBottomSheet`.

### 11. Hardcoded Strings in UI [FIXED]
*   **Fix:** Moved "Set", "reps", and "x" strings to `strings.xml` and resolved them via `CanvasState`.

### 12. Missing IME Padding on Canvas [FIXED]
*   **Fix:** Applied `.imePadding()` to the root layout of `CanvasScreen`.

### 13. Insecure Client-Side API Keys [FIXED]
*   **Fix:** Migrated to Firebase Vertex AI (Firebase.ai), which handles authentication via Google Services and Firebase App Check, eliminating the need for hardcoded client-side keys.

### 15 & 38. Hardcoded Strings in Features [FIXED]
*   **Fix:** Extracted remaining hardcoded strings in `WorkoutExercises` and `Canvas` Delete Dialog into `strings.xml`.

### 17. Exercise Summary Shows Only First Set [FIXED]
*   **Fix:** Implemented grouped set formatting (e.g., "3x10, 1x8 @ 80kg") in `FormatExerciseSummaryUseCase` on both platforms.

### 21. iOS Build Errors (.scribbleBodyLarge) [FIXED]
*   **Fix:** Replaced all lingering references to `.scribbleBodyLarge` with appropriate typography tokens from `CoreDesignSystem`.

### 24. Custom Headers in iOS Sheets [FIXED]
*   **Fix:** Replaced manual header views with native `NavigationStack` toolbars using `.navigationTitle()` and `ToolbarItem`.

### 34. Magic Numbers in Components [FIXED]
*   **Fix:** Defined semantic `Alphas` and missing typography tokens in `ScribbleFitTheme`, replacing raw literals in card components.

### 35. Missing Adaptive Width on Header [FIXED]
*   **Fix:** Added `.widthIn(max = 600.dp)` and `.align(Alignment.CenterHorizontally)` to the stats row in `WorkoutExercisesHeader.kt`.

### 36. File Length Thresholds Exceeded [FIXED]
*   **Fix:** Split `SettingsSections.kt` into modular component files. Refactored `CanvasStore.swift` to reduce complexity.

### 39. Inconsistent Trend Badge Tokens (iOS) [FIXED]
*   **Fix:** Added semantic `scribbleSuccess`, `scribbleWarning`, and `scribbleError` tokens to iOS theme and applied them to `TrendsSection.swift`.

### 40. Missing .uppercase() in Mapper (Android) [FIXED]
*   **Fix:** Added `.uppercase()` to enum string mapping in `SystemConfig.toDomain()`.

---

## Testing

### 16. WorkoutExercises Unit Tests [FIXED]
*   **Fix:** Implemented `WorkoutExercisesViewModelTest.kt` (Android) and `WorkoutExercisesStoreTests.swift` (iOS) covering reactive state updates and intent handling.
