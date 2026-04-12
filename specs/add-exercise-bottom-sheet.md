# Feature Specification: Add Exercise Bottom Sheet

## 1. Overview
The **Add Exercise Bottom Sheet** is a reusable UI component for manual exercise data entry. It provides a structured form to input exercise details, multiple performance sets (weight × reps), and optional session notes.

This component is shared across multiple features:
- **Canvas (Manual Mode):** To create new scribbles directly.
- **Workout Details:** To manually add missing exercises to an existing workout session.

## 2. User Stories
- **As a User**, I want to enter exercise details manually **so that** I can accurately log my training without relying on AI parsing.
- **As a User**, I want to add multiple sets for an exercise **so that** I can track my entire performance in one form.
- **As a User**, I want to add and remove sets dynamically **so that** the form reflects my actual session.
- **As a User**, I want to add session-specific notes **so that** I can record details like intensity or technique cues.

## 3. Acceptance Criteria

### 3.1 Layout and Components
- [ ] **Modal Bottom Sheet:** Follows the design system's bottom sheet styling (rounded top corners, drag handle).
- [ ] **Header:**
    - Title: "New Entry" (centered).
    - Close Icon ('X'): Dismisses the sheet without saving.
    - "SAVE" Button (right-aligned, primary color text): Validates and commits the entry.
- [ ] **Exercise Name Field:**
    - Label: "EXERCISE" (small, uppercase).
    - Input: Rounded background, placeholder "e.g. Bench Press". Required.
- [ ] **Muscle Group Field:**
    - Label: "MUSCLE GROUP" (small, uppercase).
    - Input: Rounded background, placeholder "e.g. Chest". Required.
- [ ] **Sets Section:**
    - Label: "SETS" (small, uppercase).
    - List of set rows, each containing:
        - Set number (e.g., "1").
        - Weight input: Numeric decimal, placeholder with user's unit (e.g., "lbs").
        - "x" separator symbol.
        - Reps input: Numeric integer, placeholder "reps".
        - Delete Icon ('X'): Removes the specific set row.
    - **"+ ADD SET"** Button: Appends a new empty set row to the bottom of the list.
    - Default state: Opens with **2 empty set rows**.
    - Minimum: 1 set must remain (delete icon is hidden/disabled if only one set exists).
- [ ] **Notes Field:**
    - Label: "NOTES" (small, uppercase).
    - Input: Multiline text area, placeholder "Add session notes...". Optional.

### 3.2 Saving Logic
- [ ] **Validation:**
    - Exercise Name must not be blank.
    - Muscle Group must not be blank.
    - At least one set must have both Weight > 0 and Reps > 0.
- [ ] **Output:**
    - When used in **Canvas**, creates a `Scribble` with status `SUCCESS` and structured exercise data.
    - When used in **Workout Details**, adds a new `WorkoutExercise` to the current workout session.
    - The `rawText` of the resulting entry (for display purposes) should be generated as: `[Exercise Name] [Weight][Unit] [Sets]x[Reps]`.

## 4. Development Guidelines (Android)
- **Architecture:** MVI (State, Intent, ViewModel).
- **Module:** `:feature:exercises:ui` (New shared UI module).
- **Package Structure:** `com.scribblefit.feature.exercises.ui.addexercise`.
- **UI:** 100% Jetpack Compose using `ScribbleFitTheme`.
    - Component: `AddExerciseBottomSheet`.
    - Handle keyboard visibility and numeric input focus efficiently.
- **Domain:**
    - `AddManualExerciseUseCase` (if not already in `:feature:canvas:domain`).
- **Dependency Injection:** Hilt.

## 4. Development Guidelines (iOS)
- **Architecture:** MVI (State, Intent, @Observable Store).
- **Package Structure:** `ExercisesFeature/UI/AddExercise/`.
- **UI:** 100% SwiftUI with `ScribbleFitTheme`.
    - View: `AddExerciseSheetView`.
    - Use native `TextField` with `keyboardType(.decimalPad)` for numeric inputs.
- **Domain:**
    - Swift Actor for state management during entry.

## 5. Validation
- **Unit Tests:**
    - Validation logic (empty names, invalid sets).
    - State management: adding/removing sets updates the set count correctly.
    - `rawText` generation logic.
- **UI Tests:**
    - Verify all labels and placeholders match the design.
    - Verify "+ ADD SET" appends a row.
    - Verify "SAVE" is disabled or shows errors if validation fails.
    - Verify dismissing the sheet clears the state.
