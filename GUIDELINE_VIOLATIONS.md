# Guideline Violations Audit

This document tracks deviations from the project's core mandates and architectural rules.

## Android Violations

### 1. Hardcoded Strings (Rule: "Zero hardcoded strings")
Many UI components in the `:feature:exercises:ui` module use hardcoded strings instead of resolving them via `State` lookups from `strings.xml`.
*   **`ExerciseInsightCard.kt`**:
    *   "RECOMMENDATION" (line 52)
    *   "Perform more sessions to get AI recommendations." (line 69)
*   **`TrendsSection.kt`**:
    *   "TRENDS" (line 33)
    *   "VIEW ALL" (line 42)
    *   "Current 1RM" (line 58)
    *   "Last Volume" (line 63)
    *   "IMPROVING", "STABLE", "PLATEAUED", "DECLINING" (lines 104-107)
*   **`HistorySection.kt`**:
    *   "HISTORY" (line 22)
    *   "View all tracked sessions" (line 50)
    *   "%d total sessions recorded" (line 56) - *Template usage is correct, but string itself is hardcoded.*
*   **`WeeklyStatsCard.kt`**:
    *   "WEEKLY PERFORMANCE" (line 25)
    *   "Activity" (line 37)
    *   "sessions" (line 39)
    *   "Volume" (line 43)
    *   "Max Weight" (line 48)

### 2. Business Logic in State/ViewModel (Rule: "Zero business logic in ViewModels/Stores")
*   **`WorkoutExercisesState.kt`**: The `uiModels` and `totalVolume` getters contain formatting and transformation logic. While `FormatExerciseSummaryUseCase` was extracted, it's still being instantiated and called directly within the `State` getter (lines 43-45), which violates the "pure state" principle.
*   **`CanvasViewModel.kt`**: Manual editing logic (`updateExerciseName`, `updateSetWeight`, etc.) performs direct data manipulation and debouncing. This logic should be moved to a `ManualEditScribbleUseCase`.

### 3. Design System & Tokens (Rule: "100% Jetpack Compose with :core:designsystem tokens")
*   **`TrendsSection.kt`**: Uses hardcoded colors for badges (e.g., `Color(0xFF4CAF50)`) instead of design system tokens (lines 104-107).
*   **`ExerciseInsightCard.kt`**: Uses magic number `20.sp` for emoji font size (line 47).
*   **`ScribbleFitTheme.kt`**: The typography scale in `ScribbleFitTypography` is missing several standard Material3 tokens (only defines displayLarge, headlineSmall, titleMedium, bodyMedium, labelMedium), leading developers to use `.copy()` or hardcoded sizes in components.

### 4. Screen Splitting (Rule: "Split at 300 lines into contextual components")
*   **`CanvasViewModel.kt`**: FIXED. Refactored into private intent handlers.
*   **`ScribbleRepositoryImpl.kt`**: Still contains mapping logic (Tech Debt remaining).
*   **`SettingsSections.kt`**: FIXED. Split into category-specific files in `components/`.

### 5. Pure State Principle (Rule: "Zero business logic in ViewModels/Stores")
*   **`CanvasState.kt`**: FIXED. Uses pre-mapped `ScribbleUiModel`.
*   **`WorkoutExercisesState.kt`**: FIXED. Uses pre-mapped `WorkoutExerciseUiModel`.

### 6. Hardcoded Strings (Rule: "Zero hardcoded strings")
*   **`CanvasScreen.kt`**: FIXED. Moved to `strings.xml`.

### 7. Missing .uppercase() in Mappers (Rule: "Status enums: Uppercase raw values, use .uppercase() in mappers")
*   **`Mappers.kt`**: FIXED in `SystemConfig.toDomain()`.

---

## iOS Violations

### 1. Strings Resolution (Rule: "Resolve all UI text in State/Store")
*   **`HistorySection.swift`**: Resolved in latest updates.
*   **`ExerciseInsightCard.swift`**: Resolved via parameterization.
*   **`WorkoutItem.swift`**: FIXED. Using `FormatExerciseSummaryUseCase` and localized labels.
*   **`TrendsSection.swift`**: Partially resolved via parameterization.

### 2. Repository Observation (Rule: "Reactive AsyncStream repositories")
*   **`WorkoutExercisesStore.swift`**: FIXED. Uses `workoutRepository.observeWorkout(id:)`.

### 3. Business Logic in State/Store (Rule: "Zero business logic in ViewModels/Stores")
*   **`WorkoutExercisesState.swift`**: FIXED. Formatting moved to `FormatWorkoutSummaryUseCase`.
*   **`CanvasStore.swift`**: FIXED. Refactored into private intent handlers.

### 4. Status Enums (Rule: "Uppercase raw values")
*   **`ScribbleStatus` (iOS)**: Verified.

### 5. Screen Splitting (Rule: "Split at 300 lines into contextual components")
*   **`CanvasStore.swift`**: FIXED. Refactored to reduce complexity.

### 6. Design System & Tokens (Rule: "Design System: Editorial Minimalism")
*   **`TrendsSection.swift`**: FIXED. Now using semantic tokens like `Color.scribbleSuccess`.

---

## General Violations

### 1. Certainty Levels (Rule: "Always provide the certainty level and the proof")
*   Recent PRs and changes often claim "Fixed" without explicitly stating **MODERATE** or **STRONG** and providing the proof (build output, test results).

### 2. Boy Scout Rule (Rule: "Fix completely with tests, or don't touch it")
*   The `ExerciseDetails` implementation was added without comprehensive UI tests for either platform.
*   `TECH_DEBT.md` contains items that were marked as "Fix:" but haven't been implemented yet (e.g., adaptive constraints on all stat rows).
