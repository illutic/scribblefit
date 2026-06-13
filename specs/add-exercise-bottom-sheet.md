# Feature Specification: Add Exercise Bottom Sheet

**Feature Branch**: `[add-exercise-bottom-sheet]`

**Created**: 2026-06-13

**Status**: Draft

**Input**: User description: "Convert legacy specification for Add Exercise Bottom Sheet into spec-kit format."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Enter Exercise Details Manually (Priority: P1)

As a User, I want to enter exercise details manually so that I can accurately log my training without relying on AI parsing.

**Why this priority**: It is the core function of the manual entry mode, ensuring users have a fallback if AI parsing fails or if they prefer structured input.

**Independent Test**: Can be fully tested by opening the bottom sheet, inputting valid Exercise and Muscle Group names, entering at least one valid set, and saving the entry successfully.

**Acceptance Scenarios**:

1. **Given** an empty "New Entry" bottom sheet, **When** the user inputs "Bench Press" as EXERCISE, "Chest" as MUSCLE GROUP, and 1 valid set, **Then** the "SAVE" button commits the entry successfully.
2. **Given** the bottom sheet form, **When** the user leaves EXERCISE or MUSCLE GROUP blank, **Then** the "SAVE" button is disabled or shows validation errors.

---

### User Story 2 - Manage Multiple Sets Dynamically (Priority: P1)

As a User, I want to add multiple sets for an exercise and add/remove sets dynamically so that the form reflects my actual session.

**Why this priority**: Users rarely perform a single set. Accurately tracking multiple sets (weight x reps) is essential for a fitness tracking application.

**Independent Test**: Can be fully tested by verifying the default state of the sets list, interacting with the "+ ADD SET" button, and deleting specific sets.

**Acceptance Scenarios**:

1. **Given** the bottom sheet is opened, **When** the Sets section loads, **Then** it displays exactly 2 empty set rows by default.
2. **Given** the bottom sheet, **When** the user clicks "+ ADD SET", **Then** a new empty set row is appended to the bottom of the list.
3. **Given** the bottom sheet with more than 1 set, **When** the user clicks the Delete Icon ('X') on a set, **Then** that specific set row is removed.

---

### User Story 3 - Add Session Notes (Priority: P3)

As a User, I want to add session-specific notes so that I can record details like intensity or technique cues.

**Why this priority**: It is an optional feature that adds qualitative context to the quantitative set data.

**Independent Test**: Can be fully tested by typing multi-line text into the "NOTES" field and verifying the notes are saved with the entry.

**Acceptance Scenarios**:

1. **Given** the bottom sheet, **When** the user enters text in the "NOTES" multi-line text area and saves, **Then** the text is stored along with the exercise data.

---

### Edge Cases

- What happens when a user tries to delete the only remaining set? (The delete icon must be hidden or disabled; a minimum of 1 set must remain).
- What happens when the user attempts to save without any valid sets? (Validation fails if no set has both Weight > 0 and Reps > 0).
- How does the system handle dismissing the sheet without saving? (State must be cleared entirely when the Close Icon ('X') is clicked).
- What happens if the user inputs non-numeric characters in weight/reps fields? (UI must restrict input to numeric decimal for weight and numeric integer for reps).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST provide a Modal Bottom Sheet following the design system styling (rounded top corners, drag handle).
- **FR-002**: System MUST display a Header with a centered Title "New Entry", a Close Icon ('X'), and a right-aligned "SAVE" button in primary color text.
- **FR-003**: System MUST provide "EXERCISE" and "MUSCLE GROUP" input fields with small uppercase labels, rounded backgrounds, and appropriate placeholders ("e.g. Bench Press", "e.g. Chest"). Both fields are required.
- **FR-004**: System MUST display a Sets section containing rows with a Set number, Weight input (numeric decimal), an "x" separator, Reps input (numeric integer), and a Delete Icon ('X').
- **FR-005**: System MUST provide an optional "NOTES" multi-line text area with placeholder "Add session notes...".
- **FR-006**: System MUST enforce validation before saving: Exercise Name not blank, Muscle Group not blank, and at least one set with Weight > 0 and Reps > 0.
- **FR-007**: System MUST create a `Scribble` with status `SUCCESS` and structured exercise data when used in Canvas (Manual Mode).
- **FR-008**: System MUST add a new `Exercise` to the current session when used in Scribble Details.
- **FR-009**: System MUST generate `rawText` for the entry as: `[Exercise Name] [Weight][Unit] [Sets]x[Reps]`.
- **FR-010**: System MUST implement Android UI using 100% Jetpack Compose (`ScribbleFitTheme`), MVI architecture in `:feature:exercises:ui`, and Hilt for DI, handling keyboard visibility efficiently.
- **FR-011**: System MUST implement iOS UI using 100% SwiftUI (`ScribbleFitTheme`), native `TextField` with `keyboardType(.decimalPad)` for numeric inputs, MVI with `@Observable` Store, and Swift Actor for domain state management.

### Key Entities *(include if feature involves data)*

- **Scribble**: Represents the overall training session. In Canvas mode, saving the bottom sheet creates a `Scribble` with status `SUCCESS`.
- **Exercise**: Represents the structured exercise data logged. In Scribble Details mode, saving adds a new `Exercise` to the existing session.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Unit tests verify 100% of validation logic (empty names, invalid sets) and state management correctness (set count updates accurately).
- **SC-002**: Unit tests verify 100% correctness of `rawText` generation logic.
- **SC-003**: UI Tests pass verifying all labels and placeholders match the design system.
- **SC-004**: UI Tests pass verifying "+ ADD SET" appends rows, and minimum 1 set restriction behaves correctly.
- **SC-005**: UI Tests pass verifying "SAVE" interaction behavior (disabled/errors on invalid data) and state clearance upon sheet dismissal.

## Assumptions

- User's preferred weight unit (e.g., "lbs" or "kg") is available in the environment/state to be used for placeholders.
- `AddManualExerciseUseCase` exists in `:feature:canvas:domain` or will be created there.
- The `ScribbleFitTheme` is already implemented and provides the necessary design tokens for rounded backgrounds and typography.
- Shared UI modules (`:feature:exercises:ui` for Android and `ExercisesFeature/UI/AddExercise/` for iOS) are established or can be created as part of this feature branch.
